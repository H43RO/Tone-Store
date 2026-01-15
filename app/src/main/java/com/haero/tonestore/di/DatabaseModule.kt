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
        )
            .addMigrations(
                ToneStoreDatabase.MIGRATION_1_2,
                ToneStoreDatabase.MIGRATION_2_3,
                ToneStoreDatabase.MIGRATION_3_4
            )
            .build()
    }

    // DAO
    single { get<ToneStoreDatabase>().toneSettingDao() }
    single { get<ToneStoreDatabase>().savedPedalBoardDao() }
}
