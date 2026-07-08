package com.vibecoding.dailytasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vibecoding.dailytasks.DailyTasksApp
import com.vibecoding.dailytasks.R
import com.vibecoding.dailytasks.data.TaskRepository
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var repository: TaskRepository
    private val expandedIds = mutableSetOf<Long>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as DailyTasksApp).repository

        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.historyRecyclerView)
        val emptyView = view.findViewById<View>(R.id.historyEmptyView)

        adapter = HistoryAdapter(
            expandedIds = expandedIds,
            onHeaderClick = { id ->
                if (expandedIds.contains(id)) {
                    expandedIds.remove(id)
                } else {
                    expandedIds.add(id)
                }
                adapter.notifyDataSetChanged()
            },
            sourceLabel = { source -> sourceLabel(source) },
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repository.observeSnapshots().collect { snapshots ->
                adapter.submitList(snapshots)
                emptyView.visibility = if (snapshots.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if (snapshots.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun sourceLabel(source: String): String = when (source) {
        "manual" -> getString(R.string.history_source_manual)
        "scheduled" -> getString(R.string.history_source_scheduled)
        "widget" -> getString(R.string.history_source_widget)
        "catchup" -> getString(R.string.history_source_catchup)
        else -> source
    }
}
