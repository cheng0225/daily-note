package com.vibecoding.dailytasks.widget

import android.content.Context
import com.vibecoding.dailytasks.R

/** 大便签 2×3 */
class TaskWidgetProvider : BaseTaskWidgetProvider(
    listLayout = R.layout.widget_task_list,
    itemLayout = R.layout.widget_task_item,
) {
    companion object {
        fun refreshAll(context: Context) {
            TaskWidgetProvider().refresh(context)
        }
    }
}
