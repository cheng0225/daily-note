package com.vibecoding.dailytasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.vibecoding.dailytasks.databinding.ActivityMainBinding
import com.vibecoding.dailytasks.ui.HistoryFragment
import com.vibecoding.dailytasks.ui.TasksFragment
import com.vibecoding.dailytasks.update.UpdateManager
import com.vibecoding.dailytasks.widget.WidgetRefresh
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: com.vibecoding.dailytasks.data.TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = (application as DailyTasksApp).repository

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, TasksFragment())
            }
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, TasksFragment())
                    }
                    true
                }
                R.id.nav_history -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, HistoryFragment())
                    }
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            repository.ensureDailyReset()
            ResetScheduler.schedule(this@MainActivity)
            refreshWidget()
        }

        if (savedInstanceState == null) {
            UpdateManager.checkOnLaunch(this)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            repository.ensureDailyReset()
            ResetScheduler.schedule(this@MainActivity)
            refreshWidget()
        }
    }

    private fun refreshWidget() {
        WidgetRefresh.refreshAll(this)
    }
}
