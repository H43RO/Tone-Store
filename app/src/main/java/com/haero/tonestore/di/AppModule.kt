package com.haero.tonestore.di

/**
 * 모든 Koin Module을 포함하는 리스트
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
