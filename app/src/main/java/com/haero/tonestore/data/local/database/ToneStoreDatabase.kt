package com.haero.tonestore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haero.tonestore.data.local.dao.ToneSettingDao
import com.haero.tonestore.data.local.entity.ToneSettingEntity

/**
 * Tone Store 앱의 Room Database
 */
@Database(
    entities = [ToneSettingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ToneStoreDatabase : RoomDatabase() {
    abstract fun toneSettingDao(): ToneSettingDao
    
    companion object {
        const val DATABASE_NAME = "tone_store_db"
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tone_settings ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tone_settings ADD COLUMN tagsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}
