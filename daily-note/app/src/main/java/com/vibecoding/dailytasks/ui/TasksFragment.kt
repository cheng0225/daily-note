package com.vibecoding.dailytasks.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.R
import com.vibecoding.dailytasks.ResetScheduler
import com.vibecoding.dailytasks.SettingsActivity
import com.vibecoding.dailytasks.data.ResetSnapshotSource
import com.vibecoding.dailytasks.data.TaskEntity
import com.vibecoding.dailytasks.data.TaskRepository
import com.vibecoding.dailytasks.databinding.FragmentTasksBinding
import com.vibecoding.dailytasks.widget.WidgetRefresh
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: TaskRepository
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as DailyTasksApp).repository

        adapter = TaskAdapter(
            onToggle = { task -> toggleTask(task) },
            onEdit = { task -> showTaskDialog(task) },
            onDelete = { task -> confirmDelete(task) },
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener { showTaskDialog(null) }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        binding.btnResetTime.setOnClickListener { showResetTimePicker() }
        binding.btnResetNow.setOnClickListener { resetNow() }
        binding.resetTimeText.text = getString(R.string.reset_time, repository.formatResetTime())

        viewLifecycleOwner.lifecycleScope.launch {
            repository.observeTasks().collect { tasks ->
                adapter.submitList(tasks)
                updateEmptyState(tasks)
                updateProgress(tasks)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.resetTimeText.text = getString(R.string.reset_time, repository.formatResetTime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        viewLifecycleOwner.lifecycleScope.launch {
            repository.toggleCompleted(task.id)
            refreshWidget()
        }
    }

    private fun showTaskDialog(task: TaskEntity?) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (task == null && repository.taskCount() >= TaskRepository.MAX_TASKS) {
                showMessage(getString(R.string.task_limit_reached))
                return@launch
            }

            val editText = EditText(requireContext()).apply {
                setText(task?.title.orEmpty())
                hint = getString(R.string.task_hint)
                setPadding(48, 32, 48, 16)
            }

            AlertDialog.Builder(requireContext())
                .setTitle(if (task == null) R.string.add_task else R.string.edit_task)
                .setView(editText)
                .setPositiveButton(R.string.save) { _, _ ->
                    val title = editText.text.toString()
                    viewLifecycleOwner.lifecycleScope.launch {
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
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
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
            requireContext(),
            { _, hour, minute ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.saveResetTime(hour, minute)
                    binding.resetTimeText.text = getString(
                        R.string.reset_time,
                        repository.formatResetTime(),
                    )
                    ResetScheduler.schedule(requireContext())
                    refreshWidget()
                }
            },
            time.hour,
            time.minute,
            true,
        ).show()
    }

    private fun resetNow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.resetAllTasks(ResetSnapshotSource.MANUAL)
            refreshWidget()
            showMessage(getString(R.string.reset_done))
        }
    }

    private fun refreshWidget() {
        WidgetRefresh.refreshAll(requireContext())
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
