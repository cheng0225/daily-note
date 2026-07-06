package com.vibecoding.dailytasks.widget

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.R
import kotlinx.coroutines.runBlocking

class TaskWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val itemLayout = intent.getIntExtra(EXTRA_ITEM_LAYOUT, R.layout.widget_task_item)
        return TaskRemoteViewsFactory(applicationContext, itemLayout)
    }

    private class TaskRemoteViewsFactory(
        private val context: android.content.Context,
        private val itemLayout: Int,
    ) : RemoteViewsFactory {

        private var tasks: List<com.vibecoding.dailytasks.data.TaskEntity> = emptyList()
        private var primaryColor = 0
        private var secondaryColor = 0

        override fun onCreate() = Unit

        override fun onDataSetChanged() {
            val app = context.applicationContext as DailyTasksApp
            tasks = runBlocking { app.repository.getTasks() }
            primaryColor = app.repository.getWidgetTextPrimaryColor()
            secondaryColor = app.repository.getWidgetTextSecondaryColor()
        }

        override fun onDestroy() {
            tasks = emptyList()
        }

        override fun getCount(): Int = tasks.size

        override fun getViewAt(position: Int): RemoteViews {
            val task = tasks[position]
            val views = RemoteViews(context.packageName, itemLayout)
            views.setTextViewText(R.id.widget_item_title, task.title)
            views.setTextViewText(
                R.id.widget_item_check,
                if (task.isCompleted) "☑" else "☐",
            )
            val color = if (task.isCompleted) secondaryColor else primaryColor
            views.setTextColor(R.id.widget_item_title, color)
            views.setTextColor(R.id.widget_item_check, color)

            val fillIntent = Intent().apply {
                putExtra(BaseTaskWidgetProvider.EXTRA_TASK_ID, task.id)
            }
            views.setOnClickFillInIntent(R.id.widget_item_row, fillIntent)
            return views
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = tasks[position].id

        override fun hasStableIds(): Boolean = true
    }

    companion object {
        const val EXTRA_ITEM_LAYOUT = "item_layout"
    }
}
