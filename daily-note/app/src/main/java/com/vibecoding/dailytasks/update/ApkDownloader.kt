package com.vibecoding.dailytasks.update

import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ApkDownloader {

    fun download(
        url: String,
        dest: File,
        onProgress: (Int) -> Unit,
    ) {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 30_000
            readTimeout = 60_000
            setRequestProperty("User-Agent", UpdateConfig.USER_AGENT)
        }

        try {
            val code = conn.responseCode
            if (code !in 200..299) {
                throw UpdateException("Download failed: HTTP $code")
            }

            val total = conn.contentLength
            conn.inputStream.use { input ->
                dest.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = 0
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (total > 0) {
                            onProgress((downloaded * 100 / total).coerceIn(0, 100))
                        }
                    }
                }
            }
        } finally {
            conn.disconnect()
        }
    }
}
