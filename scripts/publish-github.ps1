# Publish to GitHub and create Release
# Usage:
#   .\scripts\publish-github.ps1
#   .\scripts\publish-github.ps1 -Version "1.1"
#   .\scripts\publish-github.ps1 -NoProxy          # direct connection
#
# v2rayN default local proxy: 127.0.0.1:10808 (SOCKS5 / mixed)
# gh CLI args use ASCII only to avoid PowerShell encoding issues.

param(
    [string]$Version = "1.4",
    [string]$RepoName = "daily-note",
    [string]$ProxyHost = "127.0.0.1",
    [int]$ProxyPort = 10808,
    [ValidateSet("socks5", "http")]
    [string]$ProxyType = "socks5",
    [switch]$NoProxy
)

$Root = Split-Path $PSScriptRoot -Parent
Set-Location $Root

$RepoDescription = "Daily Note - Android desktop daily task widget"
$ReleaseTitle = "v$Version - Daily Note"

function Enable-Proxy {
    if ($NoProxy) {
        $script:GitProxyArgs = $null
        Write-Host "Proxy: disabled (-NoProxy)" -ForegroundColor DarkGray
        return
    }

    $script:ProxyUrl = if ($ProxyType -eq "socks5") {
        "socks5://${ProxyHost}:${ProxyPort}"
    } else {
        "http://${ProxyHost}:${ProxyPort}"
    }

    # gh / curl / git
    $env:ALL_PROXY = $script:ProxyUrl
    $env:HTTP_PROXY = $script:ProxyUrl
    $env:HTTPS_PROXY = $script:ProxyUrl
    $env:NO_PROXY = "localhost,127.0.0.1"

    # git.exe (per-command -c is also applied in Invoke-Git)
    $script:GitProxyArgs = @(
        "-c", "http.proxy=$($script:ProxyUrl)",
        "-c", "https.proxy=$($script:ProxyUrl)"
    )

    Write-Host "Proxy: $($script:ProxyUrl)  (v2rayN: ensure it is running)" -ForegroundColor DarkGray
}

function Invoke-External {
    param([scriptblock]$Command)
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    & $Command
    $code = $LASTEXITCODE
    $ErrorActionPreference = $prev
    return $code
}

function Invoke-Git {
    param([string[]]$GitArgs)
    if ($script:GitProxyArgs) {
        & git @GitProxyArgs @GitArgs
    } else {
        & git @GitArgs
    }
    return $LASTEXITCODE
}

Enable-Proxy

$GhDir = "C:\Program Files\GitHub CLI"
if (Test-Path "$GhDir\gh.exe") {
    $env:Path = "$GhDir;$env:Path"
}
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    Write-Host "GitHub CLI not found. Run: winget install GitHub.cli" -ForegroundColor Red
    exit 1
}

if ((Invoke-External { gh auth status }) -ne 0) {
    Write-Host "Login to GitHub in browser..." -ForegroundColor Yellow
    Write-Host "If it times out, check v2rayN is on and port $ProxyPort is open." -ForegroundColor DarkGray
    gh auth login --hostname github.com --git-protocol https --web
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Login failed. Try: v2rayN on + run script again, or -ProxyType http" -ForegroundColor Red
        exit 1
    }
}

$ApkPath = Join-Path $Root "daily-note\releases\daily-note-v$Version-debug.apk"
if (-not (Test-Path $ApkPath)) {
    Write-Host "Building APK..." -ForegroundColor Cyan
    $env:JAVA_HOME = "C:\Users\xinbo006\.jdks\jbr-17.0.14"
    Push-Location (Join-Path $Root "daily-note")
    .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) { Pop-Location; exit 1 }
    Pop-Location
}

$hasOrigin = (Invoke-External { git remote get-url origin }) -eq 0
if (-not $hasOrigin) {
    Write-Host "Linking remote $RepoName ..." -ForegroundColor Cyan
    gh repo create $RepoName --public --source=. --remote=origin --description $RepoDescription 2>$null
    if ($LASTEXITCODE -ne 0) {
        # Repo may already exist on GitHub — add remote and continue
        $user = gh api user -q .login
        git remote add origin "https://github.com/$user/$RepoName.git" 2>$null
        if (-not (git remote get-url origin 2>$null)) {
            Write-Host "Failed to link remote. Check: gh repo view $RepoName" -ForegroundColor Red
            exit 1
        }
        Write-Host "Remote linked to existing repo." -ForegroundColor DarkGray
    }
} else {
    gh repo edit --description $RepoDescription 2>$null
}

Write-Host "Pushing code..." -ForegroundColor Cyan
if ((Invoke-Git @("push", "-u", "origin", "main")) -ne 0) { exit 1 }

Invoke-Git @("tag", "-a", "v$Version", "-m", "Release v$Version") | Out-Null
if ((Invoke-Git @("push", "origin", "v$Version", "--force")) -ne 0) { exit 1 }

Write-Host "Creating release v$Version ..." -ForegroundColor Cyan
$releaseExists = (Invoke-External { gh release view "v$Version" }) -eq 0
if ($releaseExists) {
    gh release upload "v$Version" $ApkPath --clobber
    if ($LASTEXITCODE -ne 0) { exit 1 }
} else {
    gh release create "v$Version" `
        --title $ReleaseTitle `
        --notes-file CHANGELOG.md `
        $ApkPath
    if ($LASTEXITCODE -ne 0) { exit 1 }
}

$user = gh api user -q .login
Write-Host ""
Write-Host "Done: https://github.com/$user/$RepoName" -ForegroundColor Green
Write-Host "Release: https://github.com/$user/$RepoName/releases/tag/v$Version" -ForegroundColor Green
