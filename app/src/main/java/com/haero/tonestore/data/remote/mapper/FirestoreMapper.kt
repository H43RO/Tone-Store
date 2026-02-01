package com.haero.tonestore.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.haero.tonestore.domain.model.*

/**
 * Firestore DocumentSnapshot -> SharedToneSetting 변환
 */
@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toSharedToneSetting(): SharedToneSetting? {
    return try {
        val data = this.data ?: return null

        SharedToneSetting(
            id = this.id,
            authorId = data["authorId"] as? String ?: "",
            authorName = data["authorName"] as? String ?: "",
            authorPhotoUrl = data["authorPhotoUrl"] as? String,
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            toneSetting = parseToneSetting(data["toneSetting"] as? Map<String, Any>),
            likes = (data["likes"] as? Long)?.toInt() ?: 0,
            downloads = (data["downloads"] as? Long)?.toInt() ?: 0,
            commentCount = (data["commentCount"] as? Long)?.toInt() ?: 0,
            createdAt = data["createdAt"] as? Long ?: 0L,
            updatedAt = data["updatedAt"] as? Long ?: 0L,
            tags = parseGenreTags(data["tags"] as? List<String>)
        )
    } catch (e: Exception) {
        null
    }
}

/**
 * Firestore DocumentSnapshot -> ToneSetting 변환
 */
@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toToneSetting(): ToneSetting? {
    return try {
        val data = this.data ?: return null
        parseToneSetting(data.toMutableMap().apply { put("id", this@toToneSetting.id) })
    } catch (e: Exception) {
        null
    }
}

/**
 * Firestore DocumentSnapshot -> SavedPedalBoard 변환
 */
@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toSavedPedalBoard(): SavedPedalBoard? {
    return try {
        val data = this.data ?: return null
        parseSavedPedalBoard(data.toMutableMap().apply { put("id", this@toSavedPedalBoard.id) })
    } catch (e: Exception) {
        null
    }
}

/**
 * SharedToneSetting -> Firestore Map 변환
 */
fun SharedToneSetting.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "authorId" to authorId,
        "authorName" to authorName,
        "authorPhotoUrl" to authorPhotoUrl,
        "title" to title,
        "description" to description,
        "toneSetting" to toneSetting.toFirestoreMap(),
        "likes" to likes,
        "downloads" to downloads,
        "commentCount" to commentCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "tags" to tags.map { it.name }
    )
}

/**
 * ToneSetting -> Firestore Map 변환
 */
fun ToneSetting.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "songName" to songName,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "pedalBoard" to pedalBoard.toFirestoreMap(),
        "ampSetting" to ampSetting.toFirestoreMap(),
        "guitarSetting" to guitarSetting.toFirestoreMap(),
        "isFavorite" to isFavorite,
        "tags" to tags.map { it.name }
    )
}

/**
 * SavedPedalBoard -> Firestore Map 변환
 */
fun SavedPedalBoard.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "name" to name,
        "columns" to columns,
        "rows" to rows,
        "slots" to slots.map { pedal -> pedal?.toFirestoreMap() },
        "expressionPedal" to expressionPedal?.toFirestoreMap(),
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

fun PedalBoard.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "pedals" to pedals.map { it.toFirestoreMap() }
    )
}

fun Pedal.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "name" to name,
        "type" to type.name,
        "knobs" to knobs.map { it.toFirestoreMap() },
        "order" to order,
        "isEnabled" to isEnabled,
        "color" to color
    )
}

fun Knob.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "value" to value,
        "minValue" to minValue,
        "maxValue" to maxValue
    )
}

fun AmpSetting.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "ampModel" to ampModel,
        "gain" to gain,
        "bass" to bass,
        "middle" to middle,
        "treble" to treble,
        "presence" to presence,
        "reverb" to reverb,
        "masterVolume" to masterVolume
    )
}

fun GuitarSetting.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "guitarModel" to guitarModel,
        "pickupSelector" to pickupSelector.name,
        "toneKnob" to toneKnob,
        "volumeKnob" to volumeKnob
    )
}

// ===== Parse Helpers =====

@Suppress("UNCHECKED_CAST")
fun parseToneSetting(data: Map<String, Any>?): ToneSetting {
    if (data == null) return createEmptyToneSetting()

    return ToneSetting(
        id = data["id"] as? String ?: "",
        songName = data["songName"] as? String ?: "",
        createdAt = data["createdAt"] as? Long ?: 0L,
        updatedAt = data["updatedAt"] as? Long ?: 0L,
        pedalBoard = parsePedalBoard(data["pedalBoard"] as? Map<String, Any>),
        ampSetting = parseAmpSetting(data["ampSetting"] as? Map<String, Any>),
        guitarSetting = parseGuitarSetting(data["guitarSetting"] as? Map<String, Any>),
        isFavorite = data["isFavorite"] as? Boolean ?: false,
        tags = parseGenreTags(data["tags"] as? List<String>)
    )
}

@Suppress("UNCHECKED_CAST")
fun parseSavedPedalBoard(data: Map<String, Any>?): SavedPedalBoard {
    if (data == null) return SavedPedalBoard(id = "", name = "")

    val slots = (data["slots"] as? List<Map<String, Any>?>)?.map { slotData ->
        slotData?.let { parsePedal(it) }
    } ?: List(SavedPedalBoard.DEFAULT_COLUMNS * SavedPedalBoard.DEFAULT_ROWS) { null }

    return SavedPedalBoard(
        id = data["id"] as? String ?: "",
        name = data["name"] as? String ?: "",
        columns = (data["columns"] as? Long)?.toInt() ?: SavedPedalBoard.DEFAULT_COLUMNS,
        rows = (data["rows"] as? Long)?.toInt() ?: SavedPedalBoard.DEFAULT_ROWS,
        slots = slots,
        expressionPedal = (data["expressionPedal"] as? Map<String, Any>)?.let { parsePedal(it) },
        createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
        updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()
    )
}

@Suppress("UNCHECKED_CAST")
private fun parsePedalBoard(data: Map<String, Any>?): PedalBoard {
    if (data == null) return PedalBoard()

    val pedals = (data["pedals"] as? List<Map<String, Any>>)?.map { pedalData ->
        parsePedal(pedalData)
    } ?: emptyList()

    return PedalBoard(pedals = pedals)
}

@Suppress("UNCHECKED_CAST")
private fun parsePedal(pedalData: Map<String, Any>): Pedal {
    return Pedal(
        id = pedalData["id"] as? String ?: "",
        name = pedalData["name"] as? String ?: "",
        type = try {
            PedalType.valueOf(pedalData["type"] as? String ?: "CUSTOM")
        } catch (e: Exception) {
            PedalType.CUSTOM
        },
        knobs = (pedalData["knobs"] as? List<Map<String, Any>>)?.map { knobData ->
            Knob(
                name = knobData["name"] as? String ?: "",
                value = (knobData["value"] as? Number)?.toFloat() ?: 5f,
                minValue = (knobData["minValue"] as? Number)?.toFloat() ?: 0f,
                maxValue = (knobData["maxValue"] as? Number)?.toFloat() ?: 10f
            )
        } ?: emptyList(),
        order = (pedalData["order"] as? Number)?.toInt() ?: 0,
        isEnabled = pedalData["isEnabled"] as? Boolean ?: true,
        color = pedalData["color"] as? Long
    )
}

private fun parseAmpSetting(data: Map<String, Any>?): AmpSetting {
    if (data == null) return AmpSetting()

    return AmpSetting(
        ampModel = data["ampModel"] as? String,
        gain = (data["gain"] as? Number)?.toFloat() ?: 5f,
        bass = (data["bass"] as? Number)?.toFloat() ?: 5f,
        middle = (data["middle"] as? Number)?.toFloat() ?: 5f,
        treble = (data["treble"] as? Number)?.toFloat() ?: 5f,
        presence = (data["presence"] as? Number)?.toFloat() ?: 5f,
        reverb = (data["reverb"] as? Number)?.toFloat() ?: 0f,
        masterVolume = (data["masterVolume"] as? Number)?.toFloat() ?: 5f
    )
}

private fun parseGuitarSetting(data: Map<String, Any>?): GuitarSetting {
    if (data == null) return GuitarSetting()

    return GuitarSetting(
        guitarModel = data["guitarModel"] as? String,
        pickupSelector = try {
            PickupPosition.valueOf(data["pickupSelector"] as? String ?: "BRIDGE")
        } catch (e: Exception) {
            PickupPosition.BRIDGE
        },
        toneKnob = (data["toneKnob"] as? Number)?.toFloat() ?: 10f,
        volumeKnob = (data["volumeKnob"] as? Number)?.toFloat() ?: 10f
    )
}

private fun parseGenreTags(tags: List<String>?): List<GenreTag> {
    return tags?.mapNotNull { tagName ->
        try {
            GenreTag.valueOf(tagName)
        } catch (e: Exception) {
            null
        }
    } ?: emptyList()
}

private fun createEmptyToneSetting(): ToneSetting {
    return ToneSetting(
        id = "",
        songName = "",
        createdAt = 0L,
        updatedAt = 0L,
        pedalBoard = PedalBoard(),
        ampSetting = AmpSetting(),
        guitarSetting = GuitarSetting()
    )
}
