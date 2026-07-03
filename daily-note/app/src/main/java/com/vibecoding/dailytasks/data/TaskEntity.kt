package com.vibecoding.dailytasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** 任务表实体，对应 SQLite 中的 tasks 表 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sortOrder: Int,
    val isCompleted: Boolean = false,
)
