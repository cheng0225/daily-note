package com.vibecoding.dailytasks.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.RemoteViews
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.R

/** 小组件 UI 渲染：毛玻璃背景、列表绑定 */
object WidgetRenderer {

    fun buildRemoteViews(
        context: Context,
        appWidgetId: Int,
        listLayout: Int,
        itemLayout: Int,
    ): RemoteViews {
        val app = context.applicationContext as DailyTasksApp
        val repo = app.repository
        val views = RemoteViews(context.packageName, listLayout)

        val glassAlpha = (repo.getWidgetBackgroundOpacity() * 255 / 100f).toInt().coerceIn(0, 255)
        views.setInt(R.id.widget_glass_layer, "setImageAlpha", glassAlpha)

        val serviceIntent = Intent(context, TaskWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(TaskWidgetService.EXTRA_ITEM_LAYOUT, itemLayout)
            data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }
        views.setRemoteAdapter(R.id.widget_list, serviceIntent)
        views.setEmptyView(R.id.widget_list, R.id.widget_empty)

        views.setTextColor(R.id.widget_empty, repo.getWidgetTextPrimaryColor())
        val fontLevel = repo.getWidgetFontSizeLevel()
        val emptySp = if (WidgetFontSizes.isCompactItemLayout(itemLayout)) {
            WidgetFontSizes.emptyCompactSp(fontLevel)
        } else {
            WidgetFontSizes.emptyLargeSp(fontLevel)
        }
        views.setTextViewTextSize(R.id.widget_empty, TypedValue.COMPLEX_UNIT_SP, emptySp.toFloat())

        val resetSp = if (WidgetFontSizes.isCompactItemLayout(itemLayout)) {
            WidgetFontSizes.resetCompactSp(fontLevel)
        } else {
            WidgetFontSizes.resetLargeSp(fontLevel)
        }
        views.setTextViewTextSize(R.id.widget_reset_btn, TypedValue.COMPLEX_UNIT_SP, resetSp.toFloat())
        views.setTextColor(R.id.widget_reset_btn, repo.getWidgetTextSecondaryColor())

        val resetIntent = Intent(context, WidgetResetReceiver::class.java)
        views.setOnClickPendingIntent(
            R.id.widget_reset_btn,
            PendingIntent.getBroadcast(
                context,
                1,
                resetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
        )

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
        return views
    }

    fun updateWidget(
        context: Context,
        manager: AppWidgetManager,
        appWidgetId: Int,
        listLayout: Int,
        itemLayout: Int,
    ) {
        val views = buildRemoteViews(context, appWidgetId, listLayout, itemLayout)
        manager.updateAppWidget(appWidgetId, views)
        manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
    }
}
