package com.vibecoding.dailytasks.data

import androidx.room.Embedded
import androidx.room.Relation

data class SnapshotWithItems(
    @Embedded val snapshot: ResetSnapshotEntity,
    @Relation(parentColumn = "id", entityColumn = "snapshotId")
    val items: List<ResetSnapshotItemEntity>,
)
