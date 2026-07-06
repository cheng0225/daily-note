package com.vibecoding.dailytasks.update

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GithubReleaseClient {

    fun fetchLatestRelease(): ReleaseInfo {
        val conn = (URL(UpdateConfig.API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", UpdateConfig.USER_AGENT)
        }

        try {
            val code = conn.responseCode
            val body = if (code in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                val error = conn.errorStream?.bufferedReader()?.readText().orEmpty()
                throw UpdateException("GitHub API $code: $error")
            }

            return parseRelease(body)
        } finally {
            conn.disconnect()
        }
    }

    private fun parseRelease(json: String): ReleaseInfo {
        val root = JSONObject(json)
        val tag = root.getString("tag_name")
        val version = tag.removePrefix("v").trim()
        val assets = root.getJSONArray("assets")

        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            val name = asset.getString("name")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return ReleaseInfo(
                    version = version,
                    tag = tag,
                    apkUrl = asset.getString("browser_download_url"),
                    apkName = name,
                )
            }
        }

        throw UpdateException("Release has no APK asset")
    }
}

class UpdateException(message: String, cause: Throwable? = null) : Exception(message, cause)
