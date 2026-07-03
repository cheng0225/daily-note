package com.vibecoding.dailytasks

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.vibecoding.dailytasks.widget.TaskWidgetProvider

/**
 * 设置页：调整桌面便签背景透明度等。
 *
 * 学习文档：docs/06-设置.md
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var repository: com.vibecoding.dailytasks.data.TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.settings)

        repository = (application as DailyTasksApp).repository

        val slider = findViewById<Slider>(R.id.opacitySlider)
        val valueText = findViewById<TextView>(R.id.opacityValueText)

        val current = repository.getWidgetBackgroundOpacity()
        slider.value = current.toFloat()
        updateOpacityLabel(valueText, current)

        slider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            val opacity = value.toInt()
            repository.setWidgetBackgroundOpacity(opacity)
            updateOpacityLabel(valueText, opacity)
            TaskWidgetProvider.refreshAll(this)
        }
    }

    private fun updateOpacityLabel(textView: TextView, opacity: Int) {
        textView.text = getString(R.string.settings_widget_opacity_value, opacity)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
