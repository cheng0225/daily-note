package com.vibecoding.dailytasks.widget

import android.content.Context
import com.vibecoding.dailytasks.R

/** 小便签 2×2 */
class TaskWidgetCompactProvider : BaseTaskWidgetProvider(
    listLayout = R.layout.widget_task_list_compact,
    itemLayout = R.layout.widget_task_item_compact,
) {
    companion object {
        fun refreshAll(context: Context) {
            TaskWidgetCompactProvider().refresh(context)
        }
    }
}
