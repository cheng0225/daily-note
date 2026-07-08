package com.vibecoding.dailytasks.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vibecoding.dailytasks.R
import com.vibecoding.dailytasks.data.ResetSnapshotItemEntity
import com.vibecoding.dailytasks.data.SnapshotWithItems
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistoryAdapter(
    private val expandedIds: Set<Long>,
    private val onHeaderClick: (Long) -> Unit,
    private val sourceLabel: (String) -> String,
) : ListAdapter<SnapshotWithItems, HistoryAdapter.SnapshotViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_snapshot, parent, false)
        return SnapshotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnapshotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SnapshotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val header = itemView.findViewById<View>(R.id.snapshotHeader)
        private val timeText = itemView.findViewById<TextView>(R.id.snapshotTimeText)
        private val sourceText = itemView.findViewById<TextView>(R.id.snapshotSourceText)
        private val progressText = itemView.findViewById<TextView>(R.id.snapshotProgressText)
        private val itemsContainer = itemView.findViewById<ViewGroup>(R.id.snapshotItemsContainer)

        fun bind(data: SnapshotWithItems) {
            val snapshot = data.snapshot
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val localTime = Instant.ofEpochMilli(snapshot.resetAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            timeText.text = formatter.format(localTime)
            sourceText.text = sourceLabel(snapshot.source)
            progressText.text = itemView.context.getString(
                R.string.history_progress,
                snapshot.doneCount,
                snapshot.totalCount,
            )

            val expanded = expandedIds.contains(snapshot.id)
            itemsContainer.visibility = if (expanded) View.VISIBLE else View.GONE
            itemsContainer.removeAllViews()

            if (expanded) {
                val sortedItems = data.items.sortedWith(
                    compareBy<ResetSnapshotItemEntity> { it.sortOrder }.thenBy { it.id },
                )
                val inflater = LayoutInflater.from(itemView.context)
                for (item in sortedItems) {
                    val row = inflater.inflate(R.layout.item_history_task_readonly, itemsContainer, false)
                    val checkbox = row.findViewById<CheckBox>(R.id.checkbox)
                    val title = row.findViewById<TextView>(R.id.title)
                    checkbox.isChecked = item.isCompleted
                    title.text = item.title
                    title.paintFlags = if (item.isCompleted) {
                        title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    title.alpha = if (item.isCompleted) 0.5f else 1f
                    itemsContainer.addView(row)
                }
            }

            header.setOnClickListener { onHeaderClick(snapshot.id) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SnapshotWithItems>() {
        override fun areItemsTheSame(oldItem: SnapshotWithItems, newItem: SnapshotWithItems) =
            oldItem.snapshot.id == newItem.snapshot.id

        override fun areContentsTheSame(oldItem: SnapshotWithItems, newItem: SnapshotWithItems) =
            oldItem == newItem
    }
}
