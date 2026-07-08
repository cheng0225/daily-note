package com.vibecoding.dailytasks.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reset_snapshots (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                resetAt INTEGER NOT NULL,
                doneCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL,
                source TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reset_snapshot_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                snapshotId INTEGER NOT NULL,
                title TEXT NOT NULL,
                sortOrder INTEGER NOT NULL,
                isCompleted INTEGER NOT NULL,
                FOREIGN KEY(snapshotId) REFERENCES reset_snapshots(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_reset_snapshot_items_snapshotId ON reset_snapshot_items(snapshotId)",
        )
    }
}
