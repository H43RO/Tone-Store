package com.haero.tonestore.di

import com.haero.tonestore.data.repository.AuthRepositoryImpl
import com.haero.tonestore.data.repository.CommentRepositoryImpl
import com.haero.tonestore.data.repository.FirestoreCustomPedalRepositoryImpl
import com.haero.tonestore.data.repository.FirestoreSavedPedalBoardRepositoryImpl
import com.haero.tonestore.data.repository.FirestoreToneSettingRepositoryImpl
import com.haero.tonestore.data.repository.SharedToneSettingRepositoryImpl
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.repository.CommentRepository
import com.haero.tonestore.domain.repository.CustomPedalRepository
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import com.haero.tonestore.domain.repository.ToneSettingRepository
import org.koin.dsl.module

/**
 * Repository 관련 Koin Module
 *
 * 모든 데이터는 Firestore에 저장되어 기기 변경 시에도 데이터 유지
 */
val repositoryModule = module {

    // Firestore 기반 Repository (로그인 필요)
    single<ToneSettingRepository> { FirestoreToneSettingRepositoryImpl(get(), get()) }
    single<SavedPedalBoardRepository> { FirestoreSavedPedalBoardRepositoryImpl(get(), get()) }
    single<CustomPedalRepository> { FirestoreCustomPedalRepositoryImpl(get(), get()) }

    // Auth & Community
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<SharedToneSettingRepository> { SharedToneSettingRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
}
