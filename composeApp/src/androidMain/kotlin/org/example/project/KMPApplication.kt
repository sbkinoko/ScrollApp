package org.example.project

import AppModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KMPApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@KMPApplication)
            modules(AppModule)
        }
    }
}
