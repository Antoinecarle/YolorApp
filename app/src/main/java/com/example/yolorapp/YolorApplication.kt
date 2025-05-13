package com.example.yolorapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application principale pour YOLOR Android.
 * Initialise les composants globaux et les librairies.
 */
@HiltAndroidApp
class YolorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialisation de Timber pour le logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Autres initialisations globales si nécessaire
    }
} 