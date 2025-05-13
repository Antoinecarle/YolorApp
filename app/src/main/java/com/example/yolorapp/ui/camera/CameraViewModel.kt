package com.example.yolorapp.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.yolorapp.data.UserPreferencesRepository
import com.example.yolorapp.data.model.DetectionResults
import com.example.yolorapp.ml.YolorInferenceEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inferenceEngine: YolorInferenceEngine,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    sealed class ProcessingResult {
        data class Success(val detectionResults: DetectionResults) : ProcessingResult()
        data class Error(val message: String) : ProcessingResult()
    }

    /**
     * Processe une image capturée pour la détection d'objets.
     *
     * @param imageUri URI de l'image à analyser
     * @return Flow de ProcessingResult avec les résultats de détection ou une erreur
     */
    fun processImage(imageUri: Uri): Flow<ProcessingResult> = flow {
        try {
            // Charger les préférences utilisateur
            val userPreferences = preferencesRepository.userPreferencesFlow.first()
            
            // Charger l'image depuis l'URI
            val bitmap = loadBitmapFromUri(imageUri)
                ?: throw IOException("Impossible de charger l'image")
                
            // Exécuter la détection d'objets
            val detectionResults = inferenceEngine.detectObjects(bitmap, userPreferences)
            
            if (detectionResults != null) {
                emit(ProcessingResult.Success(detectionResults))
            } else {
                emit(ProcessingResult.Error("Échec de la détection d'objets"))
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du traitement de l'image")
            emit(ProcessingResult.Error("Erreur: ${e.localizedMessage ?: "Inconnue"}"))
        }
    }.flowOn(Dispatchers.IO)
    
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