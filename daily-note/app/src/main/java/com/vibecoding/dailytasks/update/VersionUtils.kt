package com.vibecoding.dailytasks.update

object VersionUtils {

    fun isNewer(remote: String, local: String): Boolean {
        val remoteParts = parseParts(remote)
        val localParts = parseParts(local)
        val length = maxOf(remoteParts.size, localParts.size)

        for (i in 0 until length) {
            val remoteValue = remoteParts.getOrElse(i) { 0 }
            val localValue = localParts.getOrElse(i) { 0 }
            if (remoteValue > localValue) return true
            if (remoteValue < localValue) return false
        }
        return false
    }

    private fun parseParts(version: String): List<Int> =
        version.removePrefix("v").trim().split(".").map { part ->
            part.filter { it.isDigit() }.toIntOrNull() ?: 0
        }
}
