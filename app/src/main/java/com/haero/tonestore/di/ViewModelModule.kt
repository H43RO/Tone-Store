package com.haero.tonestore.di

import com.haero.tonestore.presentation.viewmodel.CreateToneViewModel
import com.haero.tonestore.presentation.viewmodel.DetailViewModel
import com.haero.tonestore.presentation.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * ViewModel 관련 Koin Module
 */
val viewModelModule = module {
    
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CreateToneViewModel(get(), get(), get()) }
    viewModel { DetailViewModel(get(), get()) }
}
