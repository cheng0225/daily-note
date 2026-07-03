package com.vibecoding.dailytasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.vibecoding.dailytasks.receiver.DailyResetReceiver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 每日重置闹钟调度器。
 *
 * 计算「下一次重置时刻」并注册 [AlarmManager]；
 * 修改重置时间后重新调用 [schedule] 即可，类似改闹钟。
 *
 * 学习文档：docs/05-每日重置.md
 */
object ResetScheduler {

    fun schedule(context: Context) {
        val app = context.applicationContext as DailyTasksApp
        val resetTime = app.repository.getResetTime()

        val now = LocalDateTime.now()
        var trigger = now.withHour(resetTime.hour).withMinute(resetTime.minute).withSecond(0).withNano(0)
        if (!trigger.isAfter(now)) {
            trigger = trigger.plusDays(1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val triggerAtMillis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    private const val REQUEST_CODE = 1001
}
