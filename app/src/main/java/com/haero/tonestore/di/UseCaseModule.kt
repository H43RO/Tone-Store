package com.haero.tonestore.di

import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetSavedPedalBoardByIdUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.domain.usecase.SavePedalBoardUseCase
import com.haero.tonestore.domain.usecase.SaveToneSettingUseCase
import com.haero.tonestore.domain.usecase.ToggleFavoriteUseCase
import org.koin.dsl.module

/**
 * UseCase 관련 Koin Module
 */
val useCaseModule = module {

    // ToneSetting UseCases
    factory { GetAllToneSettingsUseCase(get()) }
    factory { GetToneSettingByIdUseCase(get()) }
    factory { SaveToneSettingUseCase(get()) }
    factory { DeleteToneSettingUseCase(get()) }
    factory { GetPresetPedalsUseCase() }
    factory { ToggleFavoriteUseCase(get()) }

    // SavedPedalBoard UseCases
    factory { GetAllSavedPedalBoardsUseCase(get()) }
    factory { GetSavedPedalBoardByIdUseCase(get()) }
    factory { SavePedalBoardUseCase(get()) }
    factory { DeleteSavedPedalBoardUseCase(get()) }
}
