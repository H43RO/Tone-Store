package com.haero.tonestore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haero.tonestore.data.local.dao.SavedPedalBoardDao
import com.haero.tonestore.data.local.dao.ToneSettingDao
import com.haero.tonestore.data.local.entity.SavedPedalBoardEntity
import com.haero.tonestore.data.local.entity.ToneSettingEntity

/**
 * Tone Store 앱의 Room Database
 */
@Database(
    entities = [ToneSettingEntity::class, SavedPedalBoardEntity::class],
    version = 5,
    exportSchema = false
)
abstract class ToneStoreDatabase : RoomDatabase() {
    abstract fun toneSettingDao(): ToneSettingDao
    abstract fun savedPedalBoardDao(): SavedPedalBoardDao

    companion object {
        const val DATABASE_NAME = "tone_store_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tone_settings ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tone_settings ADD COLUMN tagsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_pedal_boards (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        slotsJson TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // columns, rows 컬럼 추가 (기본값 5x2)
                db.execSQL("ALTER TABLE saved_pedal_boards ADD COLUMN columns INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE saved_pedal_boards ADD COLUMN rows INTEGER NOT NULL DEFAULT 2")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // expressionPedalJson 컬럼 추가 (선택사항)
                db.execSQL("ALTER TABLE saved_pedal_boards ADD COLUMN expressionPedalJson TEXT DEFAULT NULL")
            }
        }
    }
}
