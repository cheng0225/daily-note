package com.vibecoding.dailytasks.widget

/** 桌面便签字号：0 为当前默认（最小），共 4 档 */
object WidgetFontSizes {

    const val MIN_LEVEL = 0
    const val MAX_LEVEL = 3
    const val DEFAULT_LEVEL = 0

    private val LARGE_TITLE = intArrayOf(14, 16, 18, 20)
    private val LARGE_CHECK = intArrayOf(18, 20, 22, 24)
    private val COMPACT_TITLE = intArrayOf(12, 14, 16, 18)
    private val COMPACT_CHECK = intArrayOf(15, 17, 19, 21)
    private val EMPTY_LARGE = intArrayOf(14, 16, 18, 20)
    private val EMPTY_COMPACT = intArrayOf(12, 14, 16, 18)

    fun coerceLevel(level: Int): Int = level.coerceIn(MIN_LEVEL, MAX_LEVEL)

    fun largeTitleSp(level: Int): Int = LARGE_TITLE[coerceLevel(level)]

    fun largeCheckSp(level: Int): Int = LARGE_CHECK[coerceLevel(level)]

    fun compactTitleSp(level: Int): Int = COMPACT_TITLE[coerceLevel(level)]

    fun compactCheckSp(level: Int): Int = COMPACT_CHECK[coerceLevel(level)]

    fun emptyLargeSp(level: Int): Int = EMPTY_LARGE[coerceLevel(level)]

    fun emptyCompactSp(level: Int): Int = EMPTY_COMPACT[coerceLevel(level)]

    fun isCompactItemLayout(itemLayout: Int): Boolean =
        itemLayout == com.vibecoding.dailytasks.R.layout.widget_task_item_compact
}
