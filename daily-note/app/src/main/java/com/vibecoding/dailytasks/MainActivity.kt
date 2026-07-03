package com.vibecoding.dailytasks

import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vibecoding.dailytasks.data.TaskEntity
import com.vibecoding.dailytasks.data.TaskRepository
import com.vibecoding.dailytasks.databinding.ActivityMainBinding
import com.vibecoding.dailytasks.ui.TaskAdapter
import com.vibecoding.dailytasks.widget.TaskWidgetProvider
import kotlinx.coroutines.launch

/**
 * 主界面：任务增删改、打钩、设置重置时间。
 *
 * 数据通过 [TaskRepository.observeTasks] 自动刷新列表；
 * 每次变更后调用 [refreshWidget] 同步桌面小组件。
 *
 * 学习文档：docs/03-主界面.md
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: TaskRepository
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as DailyTasksApp
        repository = app.repository

        adapter = TaskAdapter(
            onToggle = { task -> toggleTask(task) },
            onEdit = { task -> showTaskDialog(task) },
            onDelete = { task -> confirmDelete(task) },
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener { showTaskDialog(null) }
        binding.btnSettings.setOnClickListener {
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }
        binding.btnResetTime.setOnClickListener { showResetTimePicker() }
        binding.btnResetNow.setOnClickListener { resetNow() }
        binding.resetTimeText.text = getString(R.string.reset_time, repository.formatResetTime())

        lifecycleScope.launch {
            repository.ensureDailyReset()
            refreshWidget()
        }

        lifecycleScope.launch {
            repository.observeTasks().collect { tasks ->
                adapter.submitList(tasks)
                updateEmptyState(tasks)
                updateProgress(tasks)
            }
        }
    }

    private fun updateEmptyState(tasks: List<TaskEntity>) {
        binding.emptyView.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateProgress(tasks: List<TaskEntity>) {
        val done = tasks.count { it.isCompleted }
        binding.progressText.text = getString(R.string.progress_format, done, tasks.size)
    }

    private fun toggleTask(task: TaskEntity) {
        lifecycleScope.launch {
            repository.toggleCompleted(task.id)
            refreshWidget()
        }
    }

    private fun showTaskDialog(task: TaskEntity?) {
        lifecycleScope.launch {
            if (task == null && repository.taskCount() >= TaskRepository.MAX_TASKS) {
                showMessage(getString(R.string.task_limit_reached))
                return@launch
            }

            val editText = EditText(this@MainActivity).apply {
                setText(task?.title.orEmpty())
                hint = getString(R.string.task_hint)
                setPadding(48, 32, 48, 16)
            }

            AlertDialog.Builder(this@MainActivity)
                .setTitle(if (task == null) R.string.add_task else R.string.edit_task)
                .setView(editText)
                .setPositiveButton(R.string.save) { _, _ ->
                    val title = editText.text.toString()
                    lifecycleScope.launch {
                        val ok = if (task == null) {
                            repository.addTask(title)
                        } else {
                            repository.updateTitle(task.id, title)
                        }
                        if (!ok) {
                            showMessage(getString(R.string.invalid_task))
                        } else {
                            refreshWidget()
                        }
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun confirmDelete(task: TaskEntity) {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    repository.deleteTask(task.id)
                    refreshWidget()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showResetTimePicker() {
        val time = repository.getResetTime()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                lifecycleScope.launch {
                    repository.saveResetTime(hour, minute)
                    binding.resetTimeText.text = getString(
                        R.string.reset_time,
                        repository.formatResetTime(),
                    )
                    ResetScheduler.schedule(this@MainActivity)
                    refreshWidget()
                }
            },
            time.hour,
            time.minute,
            true,
        ).show()
    }

    private fun resetNow() {
        lifecycleScope.launch {
            repository.resetAllTasks()
            refreshWidget()
            showMessage(getString(R.string.reset_done))
        }
    }

    /** 通知所有已放置的小组件刷新显示 */
    private fun refreshWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(
            android.content.ComponentName(this, TaskWidgetProvider::class.java),
        )
        if (ids.isNotEmpty()) {
            TaskWidgetProvider.updateAll(this, manager, ids)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.resetTimeText.text = getString(R.string.reset_time, repository.formatResetTime())
        lifecycleScope.launch {
            repository.ensureDailyReset()
            refreshWidget()
        }
    }

    private fun showMessage(message: String) {
        com.google.android.material.snackbar.Snackbar
            .make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
            .show()
    }
}
