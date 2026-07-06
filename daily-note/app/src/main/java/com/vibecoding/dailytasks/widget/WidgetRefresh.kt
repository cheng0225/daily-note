package com.vibecoding.dailytasks.widget

import android.content.Context

/** 刷新所有尺寸的小组件 */
object WidgetRefresh {
    fun refreshAll(context: Context) {
        TaskWidgetProvider.refreshAll(context)
        TaskWidgetCompactProvider.refreshAll(context)
    }
}
