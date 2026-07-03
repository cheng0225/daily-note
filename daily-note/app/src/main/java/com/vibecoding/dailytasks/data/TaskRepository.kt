package com.vibecoding.dailytasks.data

import android.content.Context
import android.graphics.Color
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * 数据层中枢：封装 Room 数据库 + SharedPreferences。
 *
 * - 任务 CRUD、打钩 → 走 [taskDao]
 * - 重置时间、上次重置日期 → 走 SharedPreferences
 *
 * 学习文档：docs/02-数据层.md
 */
class TaskRepository(context: Context, database: AppDatabase) {

    private val taskDao = database.taskDao()
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun observeTasks(): Flow<List<TaskEntity>> = taskDao.observeAll()

    suspend fun getTasks(): List<TaskEntity> = taskDao.getAll()

    suspend fun taskCount(): Int = taskDao.count()

    suspend fun addTask(title: String): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty() || taskDao.count() >= MAX_TASKS) return false
        val nextOrder = (taskDao.getAll().maxOfOrNull { it.sortOrder } ?: -1) + 1
        taskDao.insert(TaskEntity(title = trimmed, sortOrder = nextOrder))
        return true
    }

    suspend fun updateTitle(id: Long, title: String): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        val task = taskDao.getAll().find { it.id == id } ?: return false
        taskDao.update(task.copy(title = trimmed))
        return true
    }

    suspend fun toggleCompleted(id: Long) {
        val task = taskDao.getAll().find { it.id == id } ?: return
        taskDao.update(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun deleteTask(id: Long) {
        taskDao.deleteById(id)
    }

    /** 打开 App 或开机时：若已过当日重置时间且尚未重置，则自动清空打钩 */
    suspend fun ensureDailyReset() {
        val today = LocalDate.now()
        val now = LocalTime.now()
        val resetTime = getResetTime()
        val lastResetStr = prefs.getString(KEY_LAST_RESET_DATE, null)

        if (lastResetStr == null) {
            prefs.edit { putString(KEY_LAST_RESET_DATE, today.toString()) }
            return
        }

        val lastReset = LocalDate.parse(lastResetStr)
        if (!lastReset.isBefore(today)) return

        val daysSince = ChronoUnit.DAYS.between(lastReset, today)
        val shouldReset = daysSince > 1 || !now.isBefore(resetTime)

        if (shouldReset) {
            resetAllTasks()
        }
    }

    suspend fun resetAllTasks() {
        taskDao.resetAllCompleted()
        prefs.edit { putString(KEY_LAST_RESET_DATE, LocalDate.now().toString()) }
    }

    fun getResetTime(): LocalTime {
        val hour = prefs.getInt(KEY_RESET_HOUR, DEFAULT_RESET_HOUR)
        val minute = prefs.getInt(KEY_RESET_MINUTE, DEFAULT_RESET_MINUTE)
        return LocalTime.of(hour, minute)
    }

    fun formatResetTime(): String {
        val time = getResetTime()
        return String.format("%02d:%02d", time.hour, time.minute)
    }

    /** 保存重置时间，仅重新预约下一次闹钟（类似改闹钟），不立即重置 */
    fun saveResetTime(hour: Int, minute: Int) {
        prefs.edit {
            putInt(KEY_RESET_HOUR, hour)
            putInt(KEY_RESET_MINUTE, minute)
        }
    }

    /**
     * 便签背景不透明度 0～100。
     * 0 = 完全透明；100 = 不透明白色底。
     */
    fun getWidgetBackgroundOpacity(): Int =
        prefs.getInt(KEY_WIDGET_BG_OPACITY, DEFAULT_WIDGET_BG_OPACITY)

    fun setWidgetBackgroundOpacity(opacity: Int) {
        prefs.edit { putInt(KEY_WIDGET_BG_OPACITY, opacity.coerceIn(0, 100)) }
    }

    /** 转为 ARGB 颜色，供 RemoteViews 设置背景 */
    fun getWidgetBackgroundColor(): Int {
        val alpha = (getWidgetBackgroundOpacity() * 255 / 100f).toInt()
        return Color.argb(alpha, 255, 255, 255)
    }

    fun markResetDoneToday() {
        prefs.edit { putString(KEY_LAST_RESET_DATE, LocalDate.now().toString()) }
    }

    fun hasResetToday(): Boolean {
        val today = LocalDate.now().toString()
        return prefs.getString(KEY_LAST_RESET_DATE, null) == today
    }

    companion object {
        const val PREFS_NAME = "daily_tasks_settings"
        const val KEY_LAST_RESET_DATE = "last_reset_date"
        const val KEY_RESET_HOUR = "reset_hour"
        const val KEY_RESET_MINUTE = "reset_minute"
        const val KEY_WIDGET_BG_OPACITY = "widget_bg_opacity"
        const val DEFAULT_RESET_HOUR = 8
        const val DEFAULT_RESET_MINUTE = 0
        const val DEFAULT_WIDGET_BG_OPACITY = 0
        const val MAX_TASKS = 15
    }
}
