package com.vibecoding.dailytasks

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.vibecoding.dailytasks.ui.WidgetColorPicker
import com.vibecoding.dailytasks.widget.WidgetRefresh

/**
 * 设置页：毛玻璃强度、文字颜色、小组件样式。
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
        val fontSizeSlider = findViewById<Slider>(R.id.fontSizeSlider)
        val fontSizeValueText = findViewById<TextView>(R.id.fontSizeValueText)
        val btnPrimary = findViewById<MaterialButton>(R.id.btnPrimaryColor)
        val btnSecondary = findViewById<MaterialButton>(R.id.btnSecondaryColor)

        val current = repository.getWidgetBackgroundOpacity()
        slider.value = current.toFloat()
        updateOpacityLabel(valueText, current)
        val fontLevel = repository.getWidgetFontSizeLevel()
        fontSizeSlider.value = fontLevel.toFloat()
        updateFontSizeLabel(fontSizeValueText, fontLevel)
        updateColorButton(btnPrimary, repository.getWidgetTextPrimaryColor())
        updateColorButton(btnSecondary, repository.getWidgetTextSecondaryColor())

        slider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            val opacity = value.toInt()
            repository.setWidgetBackgroundOpacity(opacity)
            updateOpacityLabel(valueText, opacity)
            WidgetRefresh.refreshAll(this)
        }

        fontSizeSlider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            val level = value.toInt()
            repository.setWidgetFontSizeLevel(level)
            updateFontSizeLabel(fontSizeValueText, level)
            WidgetRefresh.refreshAll(this)
        }

        btnPrimary.setOnClickListener {
            WidgetColorPicker.show(
                this,
                getString(R.string.settings_text_primary),
                repository.getWidgetTextPrimaryColor(),
            ) { color ->
                repository.setWidgetTextPrimaryColor(color)
                updateColorButton(btnPrimary, color)
                WidgetRefresh.refreshAll(this)
            }
        }

        btnSecondary.setOnClickListener {
            WidgetColorPicker.show(
                this,
                getString(R.string.settings_text_secondary),
                repository.getWidgetTextSecondaryColor(),
            ) { color ->
                repository.setWidgetTextSecondaryColor(color)
                updateColorButton(btnSecondary, color)
                WidgetRefresh.refreshAll(this)
            }
        }
    }

    private fun updateOpacityLabel(textView: TextView, opacity: Int) {
        textView.text = getString(R.string.settings_glass_value, opacity)
    }

    private fun updateFontSizeLabel(textView: TextView, level: Int) {
        val label = when (level) {
            0 -> getString(R.string.settings_font_size_min)
            1 -> getString(R.string.settings_font_size_s)
            2 -> getString(R.string.settings_font_size_m)
            else -> getString(R.string.settings_font_size_l)
        }
        textView.text = getString(R.string.settings_font_size_value, label)
    }

    private fun updateColorButton(button: MaterialButton, color: Int) {
        button.icon = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setSize(48, 48)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
