package com.example.yolorapp.ui.result

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolorapp.data.UserPreferencesRepository
import com.example.yolorapp.data.model.DetectionResults
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Arguments passed from navigation
    private val navArgs = ResultFragmentArgs.fromSavedStateHandle(savedStateHandle)
    
    // Image and detection results
    private val _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap: StateFlow<Bitmap?> = _imageBitmap.asStateFlow()
    
    private val _detectionResults = MutableStateFlow<DetectionResults?>(null)
    val detectionResults: StateFlow<DetectionResults?> = _detectionResults.asStateFlow()
    
    private val _showConfidence = MutableStateFlow(true)
    val showConfidence: StateFlow<Boolean> = _showConfidence.asStateFlow()
    
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    init {
        // Load image and detection results
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                // Get user preferences
                val userPreferences = preferencesRepository.userPreferencesFlow.first()
                _showConfidence.value = userPreferences.showConfidence
                
                // Set detection results
                _detectionResults.value = navArgs.detectionResults
                
                // Parse image URI
                val uriString = navArgs.imageUri
                if (uriString.isNotEmpty()) {
                    val uri = Uri.parse(uriString)
                    _imageUri.value = uri
                    
                    // Load bitmap
                    val bitmap = loadBitmapFromUri(uri)
                    _imageBitmap.value = bitmap
                }
            } catch (e: Exception) {
                Timber.e(e, "Erreur lors du chargement des données de résultat")
            }
        }
    }
    
    /**
     * Charge un Bitmap à partir d'une URI.
     */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du chargement du bitmap depuis URI")
            null
        }
    }
} 