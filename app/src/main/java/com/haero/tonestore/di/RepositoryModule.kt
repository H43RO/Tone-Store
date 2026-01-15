package com.haero.tonestore.di

import com.haero.tonestore.data.repository.SavedPedalBoardRepositoryImpl
import com.haero.tonestore.data.repository.ToneSettingRepositoryImpl
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository
import com.haero.tonestore.domain.repository.ToneSettingRepository
import org.koin.dsl.module

/**
 * Repository 관련 Koin Module
 */
val repositoryModule = module {

    single<ToneSettingRepository> { ToneSettingRepositoryImpl(get()) }
    single<SavedPedalBoardRepository> { SavedPedalBoardRepositoryImpl(get()) }
}
