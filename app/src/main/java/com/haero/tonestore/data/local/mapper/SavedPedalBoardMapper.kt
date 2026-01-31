package com.haero.tonestore.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haero.tonestore.data.local.entity.SavedPedalBoardEntity
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.SavedPedalBoard

/**
 * SavedPedalBoard Entity <-> Domain 변환 Mapper
 */
object SavedPedalBoardMapper {
    private val gson = Gson()
    private val pedalListType = object : TypeToken<List<Pedal?>>() {}.type
    private val pedalType = object : TypeToken<Pedal>() {}.type

    fun toEntity(domain: SavedPedalBoard): SavedPedalBoardEntity {
        return SavedPedalBoardEntity(
            id = domain.id,
            name = domain.name,
            columns = domain.columns,
            rows = domain.rows,
            slotsJson = gson.toJson(domain.slots),
            expressionPedalJson = domain.expressionPedal?.let { gson.toJson(it) },
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: SavedPedalBoardEntity): SavedPedalBoard {
        val slots: List<Pedal?> = runCatching {
            gson.fromJson<List<Pedal?>>(entity.slotsJson, pedalListType)
        }.getOrElse {
            List(entity.columns * entity.rows) { null }
        }

        val expressionPedal: Pedal? = if (!entity.expressionPedalJson.isNullOrEmpty()) {
            runCatching {
                gson.fromJson<Pedal>(entity.expressionPedalJson, pedalType)
            }.getOrNull()
        } else {
            null
        }

        return SavedPedalBoard(
            id = entity.id,
            name = entity.name,
            columns = entity.columns,
            rows = entity.rows,
            slots = slots,
            expressionPedal = expressionPedal,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
