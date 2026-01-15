package com.haero.tonestore.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haero.tonestore.data.local.entity.SavedPedalBoardEntity
import kotlinx.coroutines.flow.Flow

/**
 * 저장된 페달보드 DAO
 */
@Dao
interface SavedPedalBoardDao {

    @Query("SELECT * FROM saved_pedal_boards ORDER BY updatedAt DESC")
    fun getAllPedalBoards(): Flow<List<SavedPedalBoardEntity>>

    @Query("SELECT * FROM saved_pedal_boards WHERE id = :id")
    suspend fun getPedalBoardById(id: String): SavedPedalBoardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedalBoard(pedalBoard: SavedPedalBoardEntity)

    @Delete
    suspend fun deletePedalBoard(pedalBoard: SavedPedalBoardEntity)

    @Query("DELETE FROM saved_pedal_boards WHERE id = :id")
    suspend fun deletePedalBoardById(id: String)
}
