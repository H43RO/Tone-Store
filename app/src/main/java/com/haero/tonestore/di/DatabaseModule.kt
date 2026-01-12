package com.haero.tonestore.di

import androidx.room.Room
import com.haero.tonestore.data.local.database.ToneStoreDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Database 관련 Koin Module
 */
val databaseModule = module {
    
    // Room Database 싱글톤
    single {
        Room.databaseBuilder(
            androidContext(),
            ToneStoreDatabase::class.java,
            ToneStoreDatabase.DATABASE_NAME
        ).build()
    }
    
    // DAO
    single { get<ToneStoreDatabase>().toneSettingDao() }
}
