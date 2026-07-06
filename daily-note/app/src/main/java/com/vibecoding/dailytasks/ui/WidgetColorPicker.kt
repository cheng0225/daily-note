package com.vibecoding.dailytasks.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

object WidgetColorPicker {

    val PRESET_COLORS = intArrayOf(
        0xDE000000.toInt(),
        0xFFFFFFFF.toInt(),
        0xFF1976D2.toInt(),
        0xFFE53935.toInt(),
        0xFF43A047.toInt(),
        0xFFFB8C00.toInt(),
        0xFF8E24AA.toInt(),
        0xFF00897B.toInt(),
        0xFF5D4037.toInt(),
        0xFF455A64.toInt(),
        0xFFFFD600.toInt(),
        0xFFEC407A.toInt(),
    )

    fun show(activity: FragmentActivity, title: String, current: Int, onPick: (Int) -> Unit) {
        val grid = GridLayout(activity).apply {
            columnCount = 4
            setPadding(24, 16, 24, 8)
        }
        val size = (activity.resources.displayMetrics.density * 44).toInt()

        lateinit var dialog: AlertDialog
        dialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(grid)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        PRESET_COLORS.forEach { color ->
            val swatch = View(activity).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(8, 8, 8, 8)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(
                        if (color == current) 4 else 2,
                        if (color == current) Color.parseColor("#1976D2") else Color.parseColor("#33000000"),
                    )
                }
                setOnClickListener {
                    onPick(color)
                    dialog.dismiss()
                }
            }
            grid.addView(swatch)
        }

        dialog.show()
    }
}
