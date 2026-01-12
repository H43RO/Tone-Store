package com.haero.tonestore

import android.app.Application
import com.haero.tonestore.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Tone Store Application 클래스
 * Koin DI 초기화
 */
class ToneStoreApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ToneStoreApp)
            modules(appModules)
        }
    }
}
