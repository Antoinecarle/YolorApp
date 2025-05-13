package com.example.yolorapp.di

import android.content.Context
import com.example.yolorapp.data.UserPreferencesRepository
import com.example.yolorapp.ml.ImageProcessor
import com.example.yolorapp.ml.ModelManager
import com.example.yolorapp.ml.YolorInferenceEngine
import com.example.yolorapp.utils.CocoClassesUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour fournir les dépendances de l'application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Fournit le repository des préférences utilisateur.
     */
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
    
    /**
     * Fournit le gestionnaire de modèle YOLOR.
     */
    @Provides
    @Singleton
    fun provideModelManager(
        @ApplicationContext context: Context
    ): ModelManager {
        return ModelManager(context)
    }
    
    /**
     * Fournit le processeur d'images.
     */
    @Provides
    @Singleton
    fun provideImageProcessor(): ImageProcessor {
        return ImageProcessor()
    }
    
    /**
     * Fournit l'utilitaire pour les classes COCO.
     */
    @Provides
    @Singleton
    fun provideCocoClassesUtils(
        @ApplicationContext context: Context
    ): CocoClassesUtils {
        return CocoClassesUtils(context)
    }
    
    /**
     * Fournit le moteur d'inférence YOLOR.
     */
    @Provides
    @Singleton
    fun provideYolorInferenceEngine(
        modelManager: ModelManager,
        imageProcessor: ImageProcessor,
        cocoClassesUtils: CocoClassesUtils
    ): YolorInferenceEngine {
        return YolorInferenceEngine(modelManager, imageProcessor, cocoClassesUtils)
    }
} 