package com.vibecoding.dailytasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.vibecoding.dailytasks.receiver.DailyResetReceiver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 每日重置闹钟调度器。
 *
 * 使用 [AlarmManager.setAlarmClock] 提高在国产 ROM 上的准时触发率；
 * 修改重置时间后重新调用 [schedule] 即可。
 */
object ResetScheduler {

    fun schedule(context: Context) {
        val appContext = context.applicationContext
        val app = appContext as DailyTasksApp
        val resetTime = app.repository.getResetTime()

        val now = LocalDateTime.now()
        var trigger = now.withHour(resetTime.hour).withMinute(resetTime.minute).withSecond(0).withNano(0)
        if (!trigger.isAfter(now)) {
            trigger = trigger.plusDays(1)
        }

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(appContext, DailyResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            REQUEST_CODE,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.cancel(pendingIntent)

        val triggerAtMillis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val showIntent = PendingIntent.getActivity(
            appContext,
            REQUEST_CODE,
            Intent(appContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // 系统级「闹钟」优先级，Doze / 国产 ROM 省电下比普通 exact alarm 更可靠
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
            pendingIntent,
        )
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

    private const val REQUEST_CODE = 1001
}
