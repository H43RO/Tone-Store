package com.haero.tonestore.di

import com.haero.tonestore.presentation.viewmodel.CreateToneViewModel
import com.haero.tonestore.presentation.viewmodel.DetailViewModel
import com.haero.tonestore.presentation.viewmodel.HomeViewModel
import com.haero.tonestore.presentation.viewmodel.PedalBoardListViewModel
import com.haero.tonestore.presentation.viewmodel.PedalBoardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * ViewModel 관련 Koin Module
 */
val viewModelModule = module {
    
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { CreateToneViewModel(get(), get(), get(), get()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { PedalBoardViewModel(get(), get(), get(), get()) }
    viewModel { PedalBoardListViewModel(get(), get()) }
}
