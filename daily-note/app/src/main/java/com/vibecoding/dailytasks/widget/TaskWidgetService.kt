package com.vibecoding.dailytasks.widget

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.R
import kotlinx.coroutines.runBlocking

/**
 * 为小组件 ListView 提供数据的 RemoteViewsService。
 *
 * [TaskRemoteViewsFactory] 在 onDataSetChanged 时从数据库读取任务，
 * 在 getViewAt 中为每一行构建 RemoteViews。
 *
 * 学习文档：docs/04-桌面小组件.md
 */
class TaskWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TaskRemoteViewsFactory(applicationContext)
    }

    private class TaskRemoteViewsFactory(
        private val context: android.content.Context,
    ) : RemoteViewsFactory {

        private var tasks: List<com.vibecoding.dailytasks.data.TaskEntity> = emptyList()

        override fun onCreate() = Unit

        override fun onDataSetChanged() {
            val app = context.applicationContext as DailyTasksApp
            tasks = runBlocking { app.repository.getTasks() }
        }

        override fun onDestroy() {
            tasks = emptyList()
        }

        override fun getCount(): Int = tasks.size

        override fun getViewAt(position: Int): RemoteViews {
            val task = tasks[position]
            val views = RemoteViews(context.packageName, R.layout.widget_task_item)
            views.setTextViewText(R.id.widget_item_title, task.title)
            views.setTextViewText(
                R.id.widget_item_check,
                if (task.isCompleted) "☑" else "☐",
            )
            // 已完成：浅色；未完成：亮色（配合透明背景 + 文字阴影）
            val titleColor = if (task.isCompleted) {
                R.color.widget_text_secondary
            } else {
                R.color.widget_text_primary
            }
            views.setTextColor(R.id.widget_item_title, context.getColor(titleColor))
            views.setTextColor(R.id.widget_item_check, context.getColor(titleColor))

            val fillIntent = Intent().apply {
                putExtra(TaskWidgetProvider.EXTRA_TASK_ID, task.id)
            }
            views.setOnClickFillInIntent(R.id.widget_item_row, fillIntent)
            return views
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = tasks[position].id

        override fun hasStableIds(): Boolean = true
    }
}
