package com.vibecoding.dailytasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.vibecoding.dailytasks.receiver.DailyResetReceiver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 每日重置闹钟调度器。
 *
 * 优先 [AlarmManager.setAlarmClock]；失败时降级，且绝不因注册失败导致 App 崩溃。
 */
object ResetScheduler {

    private const val TAG = "ResetScheduler"
    private const val REQUEST_CODE_ALARM = 1001
    private const val REQUEST_CODE_SHOW = 1002

    fun schedule(context: Context) {
        try {
            val appContext = context.applicationContext
            val app = appContext as DailyTasksApp
            val resetTime = app.repository.getResetTime()

            val now = LocalDateTime.now()
            var trigger = now.withHour(resetTime.hour).withMinute(resetTime.minute)
                .withSecond(0).withNano(0)
            if (!trigger.isAfter(now)) {
                trigger = trigger.plusDays(1)
            }

            val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val receiverIntent = Intent(appContext, DailyResetReceiver::class.java)
            val alarmIntent = PendingIntent.getBroadcast(
                appContext,
                REQUEST_CODE_ALARM,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            alarmManager.cancel(alarmIntent)

            val triggerAtMillis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            if (!scheduleAlarmClock(appContext, alarmManager, alarmIntent, triggerAtMillis)) {
                scheduleExactFallback(alarmManager, alarmIntent, triggerAtMillis)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to schedule daily reset", e)
        }
    }

    private fun scheduleAlarmClock(
        context: Context,
        alarmManager: AlarmManager,
        alarmIntent: PendingIntent,
        triggerAtMillis: Long,
    ): Boolean {
        return try {
            val showIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE_SHOW,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                alarmIntent,
            )
            true
        } catch (e: Exception) {
            Log.w(TAG, "setAlarmClock failed, trying fallback", e)
            false
        }
    }

    private fun scheduleExactFallback(
        alarmManager: AlarmManager,
        alarmIntent: PendingIntent,
        triggerAtMillis: Long,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    alarmIntent,
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    alarmIntent,
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                alarmIntent,
            )
        }
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
