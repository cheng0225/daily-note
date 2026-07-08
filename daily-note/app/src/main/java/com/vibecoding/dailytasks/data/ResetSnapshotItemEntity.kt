package com.vibecoding.dailytasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reset_snapshot_items",
    foreignKeys = [
        ForeignKey(
            entity = ResetSnapshotEntity::class,
            parentColumns = ["id"],
            childColumns = ["snapshotId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("snapshotId")],
)
data class ResetSnapshotItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val snapshotId: Long,
    val title: String,
    val sortOrder: Int,
    val isCompleted: Boolean,
)
