package com.vibecoding.dailytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.ResetScheduler
import com.vibecoding.dailytasks.widget.TaskWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 每日重置闹钟触发：清空打钩 → 刷新小组件 → 注册明天闹钟 */
class DailyResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as DailyTasksApp
                app.repository.resetAllTasks()
                TaskWidgetProvider.refreshAll(context)
                ResetScheduler.schedule(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
