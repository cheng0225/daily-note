package com.vibecoding.dailytasks.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.data.ResetSnapshotSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 桌面便签「立即重置」按钮 */
class WidgetResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as DailyTasksApp
                app.repository.resetAllTasks(ResetSnapshotSource.WIDGET)
                WidgetRefresh.refreshAll(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
