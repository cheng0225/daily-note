package com.vibecoding.dailytasks.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.vibecoding.dailytasks.MainActivity
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
            val views = RemoteViews(context.packageName, R.layout.widget_task_list)

            // RemoteViews 列表：由 TaskWidgetService 提供每一行数据
            val serviceIntent = Intent(context, TaskWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            // 点击标题 → 打开 App
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPending = PendingIntent.getActivity(
                context,
                appWidgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_title, openAppPending)

            // 点击任务行 → WidgetToggleReceiver 切换打钩（模板 + 填充模式）
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
