package com.example.yolorapp.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.SystemClock
import com.example.yolorapp.data.model.Detection
import com.example.yolorapp.data.model.DetectionResults
import com.example.yolorapp.data.model.UserPreferences
import com.example.yolorapp.utils.CocoClassesUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

/**
 * Moteur d'inférence pour le modèle YOLOv8.
 * Gère la détection d'objets à partir d'images en utilisant PyTorch Mobile.
 */
@Singleton
class YolorInferenceEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val modelManager: ModelManager,
    private val imageProcessor: ImageProcessor,
    private val cocoClassesUtils: CocoClassesUtils
) {
    
    private var module: Module? = null
    
    /**
     * Initialise le modèle PyTorch.
     */
    private fun initPyTorchModel(): Boolean {
        return try {
            if (module == null) {
                val modelPath = assetFilePath(MODEL_FILE)
                module = Module.load(modelPath)
                Timber.d("Modèle PyTorch chargé avec succès: $MODEL_FILE")
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du chargement du modèle PyTorch")
            false
        }
    }
    
    /**
     * Copie le fichier de modèle depuis les assets vers le stockage local.
     */
    private fun assetFilePath(assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (!file.exists()) {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        }
        return file.absolutePath
    }
    
    /**
     * Effectue une détection d'objets sur une image en utilisant PyTorch Mobile.
     */
    fun detectObjects(
        bitmap: Bitmap,
        userPreferences: UserPreferences
    ): DetectionResults? {
        try {
            // Initialiser le modèle PyTorch
            if (!initPyTorchModel()) {
                Timber.e("Impossible de charger le modèle PyTorch")
                return null
            }
            
            // Prétraiter l'image pour PyTorch
            val startTime = SystemClock.elapsedRealtime()
            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                floatArrayOf(0.0f, 0.0f, 0.0f),
                floatArrayOf(1.0f, 1.0f, 1.0f)
            )
            
            // Exécuter l'inférence
            val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()
            val inferenceTime = SystemClock.elapsedRealtime() - startTime
            
            // Post-traiter les résultats
            val output = outputTensor.dataAsFloatArray
            val detections = postprocessResults(
                output,
                bitmap.width,
                bitmap.height,
                userPreferences.confidenceThreshold
            )
            
            Timber.d("Détection terminée: ${detections.size} objets trouvés en $inferenceTime ms")
            
            return DetectionResults(
                detections = detections,
                inferenceTime = inferenceTime,
                imageWidth = bitmap.width,
                imageHeight = bitmap.height,
                confidenceThreshold = userPreferences.confidenceThreshold
            )
            
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors de la détection d'objets avec PyTorch")
            return null
        }
    }
    
    /**
     * Post-traite les résultats bruts de l'inférence PyTorch pour YOLOv8.
     */
    private fun postprocessResults(
        output: FloatArray,
        imageWidth: Int,
        imageHeight: Int,
        confidenceThreshold: Float
    ): List<Detection> {
        // Dimensions de sortie pour YOLOv8: [1, 84, 8400]
        // Où 84 = 4 (coordonnées) + 80 (classes COCO)
        val numClasses = 80
        val numBoxes = output.size / (numClasses + 4)
        val detections = mutableListOf<Detection>()
        
        Timber.d("Traitement de $numBoxes boîtes potentielles")
        
        // YOLOv8 fournit directement les scores de classe
        // Format: [x, y, w, h, class_1, class_2, ..., class_80]
        for (i in 0 until numBoxes) {
            // Trouver l'index de classe avec la confiance maximale
            var maxClass = -1
            var maxScore = 0f
            
            for (c in 0 until numClasses) {
                val classIndex = 4 + c
                val score = if (classIndex < output.size) output[i * (numClasses + 4) + classIndex] else 0f
                if (score > maxScore) {
                    maxScore = score
                    maxClass = c
                }
            }
            
            // Ignorer les détections sous le seuil de confiance
            if (maxScore < confidenceThreshold) continue
            
            // Extraire les coordonnées
            val baseIndex = i * (numClasses + 4)
            val x = output[baseIndex]
            val y = output[baseIndex + 1]
            val w = output[baseIndex + 2]
            val h = output[baseIndex + 3]
            
            // Convertir en coordonnées absolues
            val left = (x - w / 2) * imageWidth
            val top = (y - h / 2) * imageHeight
            val right = (x + w / 2) * imageWidth
            val bottom = (y + h / 2) * imageHeight
            
            // Créer un objet Detection
            detections.add(
                Detection(
                    classId = maxClass,
                    className = cocoClassesUtils.getClassName(maxClass),
                    confidence = maxScore,
                    boundingBox = RectF(
                        max(0f, left),
                        max(0f, top),
                        min(imageWidth.toFloat(), right),
                        min(imageHeight.toFloat(), bottom)
                    )
                )
            )
        }
        
        // Appliquer Non-Maximum Suppression pour éliminer les doublons
        return applyNonMaxSuppression(detections, 0.45f)
    }
    
    /**
     * Applique l'algorithme de Non-Maximum Suppression pour éliminer les boîtes redondantes.
     */
    private fun applyNonMaxSuppression(
        detections: List<Detection>,
        iouThreshold: Float
    ): List<Detection> {
        // Trier les détections par confiance décroissante
        val sortedDetections = detections.sortedByDescending { it.confidence }
        val selectedDetections = mutableListOf<Detection>()
        
        // Liste des détections à garder
        val keep = BooleanArray(sortedDetections.size) { true }
        
        for (i in sortedDetections.indices) {
            // Si cette détection a déjà été supprimée, passer à la suivante
            if (!keep[i]) continue
            
            // Ajouter cette détection aux résultats finaux
            selectedDetections.add(sortedDetections[i])
            
            // Vérifier les détections suivantes
            for (j in i + 1 until sortedDetections.size) {
                // Si cette détection a déjà été supprimée, passer à la suivante
                if (!keep[j]) continue
                
                // Si les deux détections sont de classes différentes, les conserver
                if (sortedDetections[i].classId != sortedDetections[j].classId) continue
                
                // Calculer IoU entre les deux boîtes
                val iou = calculateIoU(
                    sortedDetections[i].boundingBox,
                    sortedDetections[j].boundingBox
                )
                
                // Si IoU est supérieur au seuil, supprimer la détection de confiance inférieure
                if (iou > iouThreshold) {
                    keep[j] = false
                }
            }
        }
        
        return selectedDetections
    }
    
    /**
     * Calcule l'Intersection over Union (IoU) entre deux boîtes.
     */
    private fun calculateIoU(a: RectF, b: RectF): Float {
        // Calculer l'intersection
        val intersection = RectF()
        val intersects = intersection.setIntersect(a, b)
        
        if (!intersects) return 0f
        
        val intersectionArea = intersection.width() * intersection.height()
        
        // Calculer l'union
        val aArea = a.width() * a.height()
        val bArea = b.width() * b.height()
        val unionArea = aArea + bArea - intersectionArea
        
        // Retourner IoU
        return if (unionArea > 0) intersectionArea / unionArea else 0f
    }
    
    companion object {
        private const val MODEL_FILE = "yolov8n.pt"
    }
} 