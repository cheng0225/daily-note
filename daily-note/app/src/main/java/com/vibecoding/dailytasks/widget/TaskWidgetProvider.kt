package com.vibecoding.dailytasks.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.vibecoding.dailytasks.R

/**
 * 桌面小组件入口。
 *
 * 系统会在添加/更新小组件时回调 [onUpdate]；
 * 使用 [RemoteViews] + [TaskWidgetService] 展示可滚动的任务列表。
 *
 * 布局：res/layout/widget_task_list.xml（透明背景）
 * 学习文档：docs/04-桌面小组件.md
 */
class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateAll(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        com.vibecoding.dailytasks.ResetScheduler.schedule(context)
    }

    companion object {
        const val ACTION_TOGGLE = "com.vibecoding.dailytasks.widget.TOGGLE"
        const val EXTRA_TASK_ID = "task_id"

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, TaskWidgetProvider::class.java),
            )
            if (ids.isNotEmpty()) {
                updateAll(context, manager, ids)
            }
        }

        fun updateAll(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
        ) {
            for (id in appWidgetIds) {
                updateWidget(context, appWidgetManager, id)
            }
        }

        private fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val app = context.applicationContext as com.vibecoding.dailytasks.DailyTasksApp
            val views = RemoteViews(context.packageName, R.layout.widget_task_list)

            // 按设置页中的透明度渲染背景（0=全透明，100=白底不透明）
            views.setInt(
                R.id.widget_root,
                "setBackgroundColor",
                app.repository.getWidgetBackgroundColor(),
            )

            val serviceIntent = Intent(context, TaskWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            val toggleIntent = Intent(context, WidgetToggleReceiver::class.java)
            views.setPendingIntentTemplate(
                R.id.widget_list,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
                ),
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
        }
    }
}
