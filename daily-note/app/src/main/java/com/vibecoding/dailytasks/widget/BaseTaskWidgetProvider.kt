package com.vibecoding.dailytasks.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context

/**
 * 小组件基类：大（2×3）与小（2×2）共用渲染逻辑。
 */
abstract class BaseTaskWidgetProvider(
    private val listLayout: Int,
    private val itemLayout: Int,
) : AppWidgetProvider() {

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

    protected fun updateAll(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (id in appWidgetIds) {
            WidgetRenderer.updateWidget(context, appWidgetManager, id, listLayout, itemLayout)
        }
    }

    protected fun refresh(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        if (ids.isNotEmpty()) {
            updateAll(context, manager, ids)
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }
}
