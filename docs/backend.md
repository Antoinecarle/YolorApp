# Architecture Backend et Traitement d'Image

## Architecture générale

### Modules principaux
1. **ModelManager** - Chargement et gestion du modèle YOLOR
2. **ImageProcessor** - Prétraitement des images pour l'inférence
3. **InferenceEngine** - Exécution de l'inférence avec TensorFlow Lite ou ONNX
4. **DetectionPostProcessor** - Post-traitement des résultats d'inférence
5. **PerformanceMonitor** - Surveillance et enregistrement des métriques de performance

### Diagramme de classes
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  ModelManager   │────▶│  InferenceEngine│────▶│ PostProcessor   │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        ▲                        ▲                       │
        │                        │                       │
        │                        │                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ UserPreferences │     │ ImageProcessor  │     │   Detection     │
│                 │     │                 │     │   Results       │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        ▲                        ▲                       │
        │                        │                       │
        │                        │                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    UI Layer     │◄────┤   Repository    │◄────┤ PerformanceStats│
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## Pipeline de traitement YOLOR

### 1. Prétraitement des images
- **Redimensionnement** - Adaptation de l'image à la taille d'entrée du modèle (ex: 640x640)
- **Normalisation** - Conversion des valeurs de pixels en plage [0,1] ou [-1,1]
- **Mise en forme** - Réorganisation des canaux de couleur (RGB vs BGR)
- **Augmentation du lot** - Préparation du tenseur d'entrée avec la forme requise

```kotlin
fun preprocess(bitmap: Bitmap): FloatBuffer {
    val inputSize = 640 // Taille attendue par le modèle
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    val buffer = FloatBuffer.allocate(1 * 3 * inputSize * inputSize) // NCHW format
    
    // Normalisation et réorganisation des canaux
    for (y in 0 until inputSize) {
        for (x in 0 until inputSize) {
            val pixel = scaledBitmap.getPixel(x, y)
            // Normalisation dans [0,1]
            buffer.put((pixel and 0xFF) / 255.0f)          // R
            buffer.put((pixel shr 8 and 0xFF) / 255.0f)    // G
            buffer.put((pixel shr 16 and 0xFF) / 255.0f)   // B
        }
    }
    buffer.rewind()
    return buffer
}
```

### 2. Inférence avec TensorFlow Lite
- **Chargement du modèle** - Depuis les assets de l'application
- **Configuration des options** - Délégation à GPU/NNAPI selon disponibilité
- **Allocation de mémoire** - Tampons d'entrée et sortie
- **Exécution** - Passage de l'image prétraitée au modèle
- **Mesure de performance** - Temps d'inférence et utilisation des ressources

```kotlin
class YolorInferenceEngine(context: Context, useGPU: Boolean = false) {
    private val interpreter: Interpreter
    
    init {
        val options = Interpreter.Options()
        if (useGPU) {
            val compatList = CompatibilityList()
            if (compatList.isDelegateSupportedOnThisDevice) {
                options.addDelegate(GpuDelegate())
            }
        }
        
        val modelBuffer = loadModelFromAssets(context, "yolor_p6_640.tflite")
        interpreter = Interpreter(modelBuffer, options)
    }
    
    fun detect(inputBuffer: FloatBuffer): Map<Int, Any> {
        val outputMap = HashMap<Int, Any>()
        // Configuration des sorties selon l'architecture YOLOR
        // (boîtes, classes, scores)
        
        val startTime = SystemClock.elapsedRealtime()
        interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)
        val inferenceTime = SystemClock.elapsedRealtime() - startTime
        
        return outputMap
    }
}
```

### 3. Post-traitement des détections
- **Décodage des sorties** - Interprétation du tenseur de sortie selon le format YOLOR
- **Transformation des coordonnées** - Conversion des coordonnées relatives en absolues
- **Filtrage par confiance** - Élimination des détections sous le seuil
- **Non-Maximum Suppression (NMS)** - Élimination des détections redondantes
- **Création des objets Detection** - Pour utilisation par l'UI

```kotlin
fun postprocess(outputMap: Map<Int, Any>, imageWidth: Int, imageHeight: Int, 
                confidenceThreshold: Float = 0.25f, iouThreshold: Float = 0.45f): List<Detection> {
    
    // Extraction des données brutes
    val boxes = outputMap[0] as Array<FloatArray>
    val scores = outputMap[1] as Array<FloatArray>
    val classes = outputMap[2] as Array<IntArray>
    
    val detections = mutableListOf<Detection>()
    
    // Conversion des coordonnées et filtrage
    for (i in boxes.indices) {
        val confidence = scores[0][i]
        if (confidence >= confidenceThreshold) {
            val classId = classes[0][i]
            
            // Format YOLOR: [x_center, y_center, width, height]
            val x = boxes[0][i * 4] * imageWidth
            val y = boxes[0][i * 4 + 1] * imageHeight
            val w = boxes[0][i * 4 + 2] * imageWidth
            val h = boxes[0][i * 4 + 3] * imageHeight
            
            // Conversion en format [left, top, right, bottom]
            val left = x - w / 2
            val top = y - h / 2
            val right = x + w / 2
            val bottom = y + h / 2
            
            detections.add(Detection(
                classId = classId,
                className = getClassName(classId),
                confidence = confidence,
                boundingBox = RectF(left, top, right, bottom)
            ))
        }
    }
    
    // Application de NMS
    return applyNMS(detections, iouThreshold)
}
```

### 4. Format de données de sortie
```kotlin
data class Detection(
    val classId: Int,
    val className: String,
    val confidence: Float,
    val boundingBox: RectF
)
```

## Optimisations

### Optimisations du modèle
- **Quantification** - Conversion des poids en INT8 pour réduire la taille et améliorer les performances
- **Pruning** - Élagage des poids proches de zéro pour optimiser la taille du modèle
- **Optimisation du post-traitement** - Implémentation efficace de NMS

### Optimisations d'exécution
- **Multithreading** - Exécution du prétraitement et post-traitement sur des threads séparés
- **Délégation matérielle** - Utilisation de GPU/NNAPI quand disponible
- **Mise en cache des résultats** - Pour les images statiques analysées multiple fois

### Optimisations de mémoire
- **Réutilisation des buffers** - Pour éviter les allocations répétées
- **Stream processing** - Traitement par lots pour réduire l'empreinte mémoire

## Métriques de performance

### Collecte de métriques
- **Temps d'inférence** - Durée d'exécution de l'inférence
- **Temps de prétraitement** - Durée du prétraitement de l'image
- **Temps de post-traitement** - Durée du post-traitement des détections
- **Utilisation mémoire** - Pic d'utilisation pendant l'inférence
- **Précision vs Rappel** - Évaluation de la qualité des détections

### Journalisation et analyse
- Enregistrement des métriques dans une base de données locale
- Visualisation des tendances de performance
- Exportation des statistiques pour analyse externe

## API et intégration

### Architecture Repository
- Interface entre la couche UI et la logique métier
- Gestion asynchrone des opérations longues
- Cache des résultats et préférences utilisateur

### Interface Public
```kotlin
interface ObjectDetectionRepository {
    suspend fun detectObjects(bitmap: Bitmap): Result<List<Detection>>
    suspend fun detectObjectsFromUri(uri: Uri): Result<List<Detection>>
    fun getPerformanceStats(): Flow<PerformanceStats>
    suspend fun updateConfidenceThreshold(threshold: Float)
    suspend fun toggleGPU(useGPU: Boolean)
}
```

### Intégration avec CameraX
- Configuration de l'analyse d'image pour inférence en temps réel
- Optimisation de la capture pour l'inférence (résolution, rotation)
- Synchronisation entre prévisualisation et capture 