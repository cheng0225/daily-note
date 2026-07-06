package com.vibecoding.dailytasks.update

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

    private var autoCheckedThisSession = false

    fun checkOnLaunch(activity: AppCompatActivity) {
        if (!UpdatePreferences.isAutoCheckEnabled(activity)) return
        if (autoCheckedThisSession || activity.isFinishing) return
        autoCheckedThisSession = true
        performCheck(activity, CheckMode.AUTO)
    }

    fun checkManually(activity: AppCompatActivity) {
        if (activity.isFinishing) return
        performCheck(activity, CheckMode.MANUAL)
    }

    private enum class CheckMode { AUTO, MANUAL }

    private fun performCheck(activity: AppCompatActivity, mode: CheckMode) {
        activity.lifecycleScope.launch {
            val checkingDialog = if (mode == CheckMode.MANUAL) {
                AlertDialog.Builder(activity)
                    .setMessage(R.string.update_checking)
                    .setCancelable(false)
                    .show()
            } else {
                null
            }

            val release = try {
                withContext(Dispatchers.IO) { GithubReleaseClient.fetchLatestRelease() }
            } catch (_: Exception) {
                checkingDialog?.dismiss()
                if (mode == CheckMode.MANUAL && !activity.isFinishing) {
                    showMessage(activity, R.string.update_check_failed_title, R.string.update_check_failed_message)
                }
                return@launch
            }

            checkingDialog?.dismiss()
            if (activity.isFinishing) return@launch

            if (!VersionUtils.isNewer(release.version, BuildConfig.VERSION_NAME)) {
                if (mode == CheckMode.MANUAL) {
                    AlertDialog.Builder(activity)
                        .setTitle(R.string.update_up_to_date_title)
                        .setMessage(
                            activity.getString(
                                R.string.update_up_to_date_message,
                                BuildConfig.VERSION_NAME,
                            ),
                        )
                        .setPositiveButton(R.string.ok, null)
                        .show()
                }
                return@launch
            }

            if (mode == CheckMode.AUTO && UpdatePreferences.isSkipped(activity, release.tag)) {
                return@launch
            }

            showUpdateDialog(activity, release)
        }
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
                UpdatePreferences.markSkipped(activity, release.tag)
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
                showMessage(activity, R.string.update_failed_title, R.string.update_failed_message)
            }
        }
    }

    private fun showMessage(activity: AppCompatActivity, titleRes: Int, messageRes: Int) {
        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
