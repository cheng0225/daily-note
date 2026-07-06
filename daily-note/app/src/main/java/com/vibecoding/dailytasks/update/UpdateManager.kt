package com.vibecoding.dailytasks.update

import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vibecoding.dailytasks.BuildConfig
import com.vibecoding.dailytasks.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object UpdateManager {

    private var checkedThisSession = false

    fun checkOnLaunch(activity: AppCompatActivity) {
        if (checkedThisSession || activity.isFinishing) return
        checkedThisSession = true

        activity.lifecycleScope.launch {
            val release = try {
                withContext(Dispatchers.IO) { GithubReleaseClient.fetchLatestRelease() }
            } catch (_: Exception) {
                return@launch
            }

            if (!VersionUtils.isNewer(release.version, BuildConfig.VERSION_NAME)) return@launch
            if (isSkipped(activity, release.tag)) return@launch
            if (activity.isFinishing) return@launch

            showUpdateDialog(activity, release)
        }
    }

    private fun isSkipped(context: Context, tag: String): Boolean {
        val prefs = context.getSharedPreferences(UpdateConfig.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(UpdateConfig.KEY_SKIPPED_VERSION, null) == tag
    }

    private fun markSkipped(context: Context, tag: String) {
        context.getSharedPreferences(UpdateConfig.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(UpdateConfig.KEY_SKIPPED_VERSION, tag)
            .apply()
    }

    private fun showUpdateDialog(activity: AppCompatActivity, release: ReleaseInfo) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.update_available_title)
            .setMessage(
                activity.getString(
                    R.string.update_available_message,
                    release.version,
                    BuildConfig.VERSION_NAME,
                ),
            )
            .setPositiveButton(R.string.update_now) { _, _ ->
                startDownload(activity, release)
            }
            .setNegativeButton(R.string.update_later) { _, _ ->
                markSkipped(activity, release.tag)
            }
            .setCancelable(false)
            .show()
    }

    private fun startDownload(activity: AppCompatActivity, release: ReleaseInfo) {
        if (!ApkInstaller.canInstallPackages(activity)) {
            AlertDialog.Builder(activity)
                .setTitle(R.string.update_install_permission_title)
                .setMessage(R.string.update_install_permission_message)
                .setPositiveButton(R.string.update_open_settings) { _, _ ->
                    ApkInstaller.openInstallPermissionSettings(activity)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
            return
        }

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_update_progress, null)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.updateProgressBar)
        val progressText = dialogView.findViewById<TextView>(R.id.updateProgressText)

        val progressDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.update_downloading)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        progressDialog.show()

        activity.lifecycleScope.launch {
            val apkFile = File(activity.cacheDir, release.apkName)
            try {
                withContext(Dispatchers.IO) {
                    ApkDownloader.download(release.apkUrl, apkFile) { percent ->
                        activity.runOnUiThread {
                            progressBar.isIndeterminate = false
                            progressBar.progress = percent
                            progressText.text = activity.getString(R.string.update_progress, percent)
                        }
                    }
                }

                progressDialog.dismiss()
                if (activity.isFinishing) return@launch

                ApkInstaller.install(activity, apkFile)
            } catch (_: Exception) {
                progressDialog.dismiss()
                if (activity.isFinishing) return@launch
                apkFile.delete()

                AlertDialog.Builder(activity)
                    .setTitle(R.string.update_failed_title)
                    .setMessage(R.string.update_failed_message)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
        }
    }
}
