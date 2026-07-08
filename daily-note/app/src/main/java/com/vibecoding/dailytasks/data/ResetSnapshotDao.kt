package com.vibecoding.dailytasks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ResetSnapshotDao {

    @Transaction
    @Query("SELECT * FROM reset_snapshots ORDER BY resetAt DESC")
    fun observeAllWithItems(): Flow<List<SnapshotWithItems>>

    @Insert
    suspend fun insertSnapshot(snapshot: ResetSnapshotEntity): Long

    @Insert
    suspend fun insertItems(items: List<ResetSnapshotItemEntity>)
}
