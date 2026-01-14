package com.haero.tonestore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 저장된 페달보드 Entity
 */
@Entity(tableName = "saved_pedal_boards")
data class SavedPedalBoardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val columns: Int,
    val rows: Int,
    val slotsJson: String,  // List<Pedal?> JSON
    val createdAt: Long,
    val updatedAt: Long
)
