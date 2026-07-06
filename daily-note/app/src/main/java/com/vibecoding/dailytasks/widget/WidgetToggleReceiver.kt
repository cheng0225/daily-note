package com.vibecoding.dailytasks.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vibecoding.dailytasks.DailyTasksApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 接收小组件列表项点击，切换任务完成状态 */
class WidgetToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(BaseTaskWidgetProvider.EXTRA_TASK_ID, -1L)
        if (taskId < 0) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as DailyTasksApp
                app.repository.toggleCompleted(taskId)
                WidgetRefresh.refreshAll(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
