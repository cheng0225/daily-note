package com.vibecoding.dailytasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reset_snapshots")
data class ResetSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resetAt: Long,
    val doneCount: Int,
    val totalCount: Int,
    val source: String,
)
