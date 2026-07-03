# 发布到 GitHub 并创建 Release
# 用法：在 C:\Android 目录下执行
#   .\scripts\publish-github.ps1
#
# 首次需先登录：gh auth login

param(
    [string]$Version = "1.0.0",
    [string]$RepoName = "daily-note"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
Set-Location $Root

gh auth status 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "请先登录 GitHub：gh auth login" -ForegroundColor Yellow
    gh auth login
}

$ApkPath = Join-Path $Root "daily-note\releases\daily-note-v$Version-debug.apk"
if (-not (Test-Path $ApkPath)) {
    Write-Host "未找到 APK，正在构建..." -ForegroundColor Cyan
    $env:JAVA_HOME = "C:\Users\xinbo006\.jdks\jbr-17.0.14"
    Push-Location (Join-Path $Root "daily-note")
    .\gradlew.bat assembleDebug
    Pop-Location
}

if (-not (git remote get-url origin 2>$null)) {
    gh repo create $RepoName --public --source=. --remote=origin `
        --description "每日便签 - Android desktop daily task widget"
}

git push -u origin main

git tag -a "v$Version" -m "Release v$Version" 2>$null
git push origin "v$Version" --force

gh release create "v$Version" `
    --title "v$Version - 每日便签" `
    --notes-file CHANGELOG.md `
    $ApkPath

$user = gh api user -q .login
Write-Host ""
Write-Host "完成！https://github.com/$user/$RepoName" -ForegroundColor Green
Write-Host "Release：https://github.com/$user/$RepoName/releases/tag/v$Version" -ForegroundColor Green
