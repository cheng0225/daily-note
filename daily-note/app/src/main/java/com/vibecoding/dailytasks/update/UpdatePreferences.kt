package com.vibecoding.dailytasks.update

import android.content.Context
import androidx.core.content.edit

object UpdatePreferences {

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(UpdateConfig.PREFS_NAME, Context.MODE_PRIVATE)

    fun isAutoCheckEnabled(context: Context): Boolean =
        prefs(context).getBoolean(UpdateConfig.KEY_AUTO_CHECK, true)

    fun setAutoCheckEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit { putBoolean(UpdateConfig.KEY_AUTO_CHECK, enabled) }
    }

    fun isSkipped(context: Context, tag: String): Boolean =
        prefs(context).getString(UpdateConfig.KEY_SKIPPED_VERSION, null) == tag

    fun markSkipped(context: Context, tag: String) {
        prefs(context).edit { putString(UpdateConfig.KEY_SKIPPED_VERSION, tag) }
    }
}
