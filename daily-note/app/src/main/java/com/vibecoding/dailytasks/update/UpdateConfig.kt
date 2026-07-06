package com.vibecoding.dailytasks.update

object UpdateConfig {
    const val GITHUB_OWNER = "cheng0225"
    const val GITHUB_REPO = "daily-note"
    const val API_URL =
        "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
    const val USER_AGENT = "DailyNote-Android"
    const val PREFS_NAME = "update_prefs"
    const val KEY_SKIPPED_VERSION = "skipped_version"
    const val KEY_AUTO_CHECK = "auto_check_enabled"
}
