package com.vibecoding.dailytasks.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vibecoding.dailytasks.data.TaskEntity
import com.vibecoding.dailytasks.databinding.ItemTaskBinding

/**
 * 主界面任务列表适配器。
 * 使用 ListAdapter + DiffUtil 高效刷新；已完成任务显示删除线。
 */
class TaskAdapter(
    private val onToggle: (TaskEntity) -> Unit,
    private val onEdit: (TaskEntity) -> Unit,
    private val onDelete: (TaskEntity) -> Unit,
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.checkbox.isChecked = task.isCompleted
            binding.title.text = task.title
            binding.title.paintFlags = if (task.isCompleted) {
                binding.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            binding.title.alpha = if (task.isCompleted) 0.5f else 1f

            binding.checkbox.setOnClickListener { onToggle(task) }
            binding.title.setOnClickListener { onToggle(task) }
            binding.btnEdit.setOnClickListener { onEdit(task) }
            binding.btnDelete.setOnClickListener { onDelete(task) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}
