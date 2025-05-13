package com.example.yolorapp.ml

import android.content.Context
import android.os.SystemClock
import com.example.yolorapp.data.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gère le chargement et l'initialisation du modèle YOLOv8.
 */
@Singleton
class ModelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var modelBuffer: MappedByteBuffer? = null
    
    private var isUsingGpu = false
    private var modelName = DEFAULT_MODEL_NAME
    
    /**
     * Charge le modèle YOLOv8 à partir des assets.
     * 
     * @param useGpu indique si le GPU doit être utilisé pour l'inférence
     * @return true si le chargement a réussi, false sinon
     */
    fun loadModel(useGpu: Boolean = false): Boolean {
        try {
            // Fermer l'interpréteur existant si nécessaire
            closeInterpreter()
            
            // Charger le modèle depuis les assets
            modelBuffer = FileUtil.loadMappedFile(context, modelName)
            
            // Configurer les options d'interprétation
            val options = Interpreter.Options()
            
            // Configurer le GPU si nécessaire
            isUsingGpu = useGpu && configureGpuDelegate(options)
            
            // Créer l'interpréteur
            interpreter = Interpreter(modelBuffer!!, options)
            
            // Logger le chargement du modèle
            Timber.d("Modèle YOLOv8 chargé avec succès: $modelName, GPU: $isUsingGpu")
            
            return true
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du chargement du modèle YOLOv8")
            closeInterpreter()
            return false
        }
    }
    
    /**
     * Configure le délégué GPU pour l'inférence.
     * 
     * @param options les options de l'interpréteur
     * @return true si le GPU est disponible et configuré, false sinon
     */
    private fun configureGpuDelegate(options: Interpreter.Options): Boolean {
        val compatList = CompatibilityList()
        
        return if (compatList.isDelegateSupportedOnThisDevice) {
            gpuDelegate = GpuDelegate(
                compatList.bestOptionsForThisDevice
            )
            options.addDelegate(gpuDelegate)
            Timber.d("GPU délégué configuré avec succès")
            true
        } else {
            Timber.d("GPU non pris en charge sur cet appareil, utilisation du CPU")
            false
        }
    }
    
    /**
     * Vérifie si le modèle est chargé et prêt à être utilisé.
     */
    fun isModelLoaded(): Boolean {
        return interpreter != null && modelBuffer != null
    }
    
    /**
     * Obtient l'interpréteur actuel.
     * @throws IllegalStateException si l'interpréteur n'est pas chargé
     */
    fun getInterpreter(): Interpreter {
        return interpreter ?: throw IllegalStateException("Interpréteur non initialisé. Appelez loadModel() d'abord.")
    }
    
    /**
     * Ferme l'interpréteur et libère les ressources.
     */
    fun closeInterpreter() {
        try {
            interpreter?.close()
            gpuDelegate?.close()
            
            interpreter = null
            gpuDelegate = null
            
            Timber.d("Interpréteur fermé et ressources libérées")
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors de la fermeture de l'interpréteur")
        }
    }
    
    /**
     * Vérifie si le modèle existe dans les assets.
     */
    fun modelExists(): Boolean {
        return try {
            context.assets.open(modelName).use { it.available() > 0 }
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors de la vérification de l'existence du modèle")
            false
        }
    }
    
    /**
     * Obtient les dimensions d'entrée du modèle.
     */
    fun getInputDimensions(): IntArray {
        val interpreter = getInterpreter()
        return interpreter.getInputTensor(0).shape()
    }
    
    companion object {
        const val DEFAULT_MODEL_NAME = "yolov8n.pt"
        const val COCO_CLASSES_FILE = "coco_classes.txt"
    }
} 