package com.haero.tonestore.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.haero.tonestore.data.local.dao.ToneSettingDao
import com.haero.tonestore.data.local.entity.ToneSettingEntity

/**
 * Tone Store 앱의 Room Database
 */
@Database(
    entities = [ToneSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ToneStoreDatabase : RoomDatabase() {
    abstract fun toneSettingDao(): ToneSettingDao
    
    companion object {
        const val DATABASE_NAME = "tone_store_db"
    }
}
