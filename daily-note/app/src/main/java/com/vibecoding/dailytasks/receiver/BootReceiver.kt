package com.vibecoding.dailytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.ResetScheduler
import com.vibecoding.dailytasks.widget.WidgetRefresh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 开机后补检查：若错过重置则执行，并重新注册闹钟 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as DailyTasksApp
                app.repository.ensureDailyReset()
                WidgetRefresh.refreshAll(context)
                ResetScheduler.schedule(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
