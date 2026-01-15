package com.haero.tonestore.domain.usecase

import com.haero.tonestore.data.preset.PresetPedals
import com.haero.tonestore.domain.model.Pedal

/**
 * 프리셋 이펙터 페달 목록을 가져오는 UseCase
 */
class GetPresetPedalsUseCase {
    operator fun invoke(): List<Pedal> {
        return PresetPedals.getPresetPedals()
    }
}
