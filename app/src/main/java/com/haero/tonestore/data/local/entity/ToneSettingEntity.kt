package com.haero.tonestore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity for ToneSetting
 * 복잡한 중첩 객체들은 JSON 문자열로 직렬화하여 저장
 */
@Entity(tableName = "tone_settings")
data class ToneSettingEntity(
    @PrimaryKey
    val id: String,
    val songName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val pedalBoardJson: String,
    val ampSettingJson: String,
    val guitarSettingJson: String
)
