package com.example.yolorapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.yolorapp.data.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "detection_settings")

/**
 * Repository pour gérer les préférences utilisateur.
 * Utilise DataStore pour stocker et récupérer les préférences.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    /**
     * Obtient les préférences utilisateur sous forme de Flow.
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        val confidenceThreshold = preferences[CONFIDENCE_THRESHOLD] ?: UserPreferences.DEFAULT_CONFIDENCE_THRESHOLD
        val useGpu = preferences[USE_GPU] ?: UserPreferences.DEFAULT_USE_GPU
        val showMetrics = preferences[SHOW_METRICS] ?: UserPreferences.DEFAULT_SHOW_METRICS
        val showConfidence = preferences[SHOW_CONFIDENCE] ?: UserPreferences.DEFAULT_SHOW_CONFIDENCE
        val imageResolutionOrdinal = preferences[IMAGE_RESOLUTION] ?: UserPreferences.DEFAULT_IMAGE_RESOLUTION.ordinal
        
        UserPreferences(
            confidenceThreshold = confidenceThreshold,
            useGPU = useGpu,
            showMetrics = showMetrics,
            showConfidence = showConfidence,
            imageResolution = UserPreferences.ImageResolution.values()[imageResolutionOrdinal]
        )
    }
    
    /**
     * Met à jour le seuil de confiance.
     */
    suspend fun updateConfidenceThreshold(threshold: Float) {
        dataStore.edit { preferences ->
            preferences[CONFIDENCE_THRESHOLD] = threshold
        }
    }
    
    /**
     * Active ou désactive l'utilisation du GPU.
     */
    suspend fun updateUseGpu(useGpu: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_GPU] = useGpu
        }
    }
    
    /**
     * Active ou désactive l'affichage des métriques.
     */
    suspend fun updateShowMetrics(showMetrics: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_METRICS] = showMetrics
        }
    }
    
    /**
     * Active ou désactive l'affichage des scores de confiance.
     */
    suspend fun updateShowConfidence(showConfidence: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_CONFIDENCE] = showConfidence
        }
    }
    
    /**
     * Met à jour la résolution d'image préférée.
     */
    suspend fun updateImageResolution(resolution: UserPreferences.ImageResolution) {
        dataStore.edit { preferences ->
            preferences[IMAGE_RESOLUTION] = resolution.ordinal
        }
    }
    
    companion object {
        private val CONFIDENCE_THRESHOLD = floatPreferencesKey("confidence_threshold")
        private val USE_GPU = booleanPreferencesKey("use_gpu")
        private val SHOW_METRICS = booleanPreferencesKey("show_metrics")
        private val SHOW_CONFIDENCE = booleanPreferencesKey("show_confidence")
        private val IMAGE_RESOLUTION = intPreferencesKey("image_resolution")
    }
} 