package com.haero.tonestore.di

import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.domain.usecase.SaveToneSettingUseCase
import com.haero.tonestore.domain.usecase.ToggleFavoriteUseCase
import org.koin.dsl.module

/**
 * UseCase 관련 Koin Module
 */
val useCaseModule = module {
    
    factory { GetAllToneSettingsUseCase(get()) }
    factory { GetToneSettingByIdUseCase(get()) }
    factory { SaveToneSettingUseCase(get()) }
    factory { DeleteToneSettingUseCase(get()) }
    factory { GetPresetPedalsUseCase() }
    factory { ToggleFavoriteUseCase(get()) }
}
