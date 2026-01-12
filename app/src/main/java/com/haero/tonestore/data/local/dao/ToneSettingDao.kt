package com.haero.tonestore.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haero.tonestore.data.local.entity.ToneSettingEntity
import kotlinx.coroutines.flow.Flow

/**
 * ToneSetting 데이터 접근을 위한 DAO
 */
@Dao
interface ToneSettingDao {
    
    @Query("SELECT * FROM tone_settings ORDER BY updatedAt DESC")
    fun getAllToneSettings(): Flow<List<ToneSettingEntity>>
    
    @Query("SELECT * FROM tone_settings WHERE id = :id")
    suspend fun getToneSettingById(id: String): ToneSettingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToneSetting(setting: ToneSettingEntity)
    
    @Delete
    suspend fun deleteToneSetting(setting: ToneSettingEntity)
    
    @Query("DELETE FROM tone_settings WHERE id = :id")
    suspend fun deleteToneSettingById(id: String)
}
