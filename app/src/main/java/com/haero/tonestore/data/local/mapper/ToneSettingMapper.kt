package com.haero.tonestore.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haero.tonestore.data.local.entity.ToneSettingEntity
import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.ToneSetting

/**
 * Entity와 Domain Model 간의 변환을 담당하는 Mapper
 */
object ToneSettingMapper {
    
    private val gson = Gson()
    
    /**
     * Entity를 Domain Model로 변환
     */
    fun ToneSettingEntity.toDomain(): ToneSetting {
        return ToneSetting(
            id = id,
            songName = songName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pedalBoard = gson.fromJson(pedalBoardJson, PedalBoard::class.java),
            ampSetting = gson.fromJson(ampSettingJson, AmpSetting::class.java),
            guitarSetting = gson.fromJson(guitarSettingJson, GuitarSetting::class.java)
        )
    }
    
    /**
     * Domain Model을 Entity로 변환
     */
    fun ToneSetting.toEntity(): ToneSettingEntity {
        return ToneSettingEntity(
            id = id,
            songName = songName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pedalBoardJson = gson.toJson(pedalBoard),
            ampSettingJson = gson.toJson(ampSetting),
            guitarSettingJson = gson.toJson(guitarSetting)
        )
    }
}
