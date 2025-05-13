package com.example.yolorapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolorapp.data.UserPreferencesRepository
import com.example.yolorapp.data.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Flow des préférences utilisateur
     */
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )
    
    /**
     * Met à jour le seuil de confiance.
     */
    fun updateConfidenceThreshold(threshold: Float) {
        viewModelScope.launch {
            preferencesRepository.updateConfidenceThreshold(threshold)
        }
    }
    
    /**
     * Active ou désactive l'utilisation du GPU.
     */
    fun updateUseGpu(useGpu: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateUseGpu(useGpu)
        }
    }
    
    /**
     * Active ou désactive l'affichage des métriques.
     */
    fun updateShowMetrics(showMetrics: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowMetrics(showMetrics)
        }
    }
    
    /**
     * Active ou désactive l'affichage des scores de confiance.
     */
    fun updateShowConfidence(showConfidence: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowConfidence(showConfidence)
        }
    }
    
    /**
     * Met à jour la résolution d'image préférée.
     */
    fun updateImageResolution(resolution: UserPreferences.ImageResolution) {
        viewModelScope.launch {
            preferencesRepository.updateImageResolution(resolution)
        }
    }
} 