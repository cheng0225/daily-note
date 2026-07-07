package com.vibecoding.dailytasks

import android.app.Application
import com.vibecoding.dailytasks.data.AppDatabase
import com.vibecoding.dailytasks.data.TaskRepository

/**
 * 应用入口。持有全局唯一的 [database] 和 [repository]，
 * 在启动时注册每日重置闹钟。
 *
 * 学习文档：docs/01-项目总览.md
 */
class DailyTasksApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { TaskRepository(this, database) }

    override fun onCreate() {
        super.onCreate()
        // 延迟注册，避免 Application 启动阶段因闹钟 API 异常导致闪退
        android.os.Handler(mainLooper).post {
            ResetScheduler.schedule(this)
        }
    }
}
