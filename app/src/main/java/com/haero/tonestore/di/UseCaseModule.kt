package com.haero.tonestore.di

import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllCustomPedalsUseCase
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetSavedPedalBoardByIdUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.domain.usecase.GetUserBookmarksUseCase
import com.haero.tonestore.domain.usecase.GetUserLikesUseCase
import com.haero.tonestore.domain.usecase.GetUserPresetsUseCase
import com.haero.tonestore.domain.usecase.GetUserProfileUseCase
import com.haero.tonestore.domain.usecase.SaveCustomPedalUseCase
import com.haero.tonestore.domain.usecase.SavePedalBoardUseCase
import com.haero.tonestore.domain.usecase.SaveToneSettingUseCase
import com.haero.tonestore.domain.usecase.ToggleBookmarkUseCase
import com.haero.tonestore.domain.usecase.ToggleFavoriteUseCase
import com.haero.tonestore.domain.usecase.ToggleLikeUseCase
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

    // CustomPedal UseCases
    factory { GetAllCustomPedalsUseCase(get()) }
    factory { SaveCustomPedalUseCase(get()) }

    // Community - Like & Bookmark UseCases
    factory { ToggleLikeUseCase(get()) }
    factory { GetUserLikesUseCase(get()) }
    factory { ToggleBookmarkUseCase(get()) }
    factory { GetUserBookmarksUseCase(get()) }

    // User Profile UseCases
    factory { GetUserProfileUseCase(get()) }
    factory { GetUserPresetsUseCase(get()) }
}
