package com.example.yolorapp.data.model

/**
 * Contient les préférences utilisateur pour l'application de détection.
 *
 * @property confidenceThreshold Seuil de confiance pour la détection [0,1]
 * @property useGPU Indique si l'utilisateur souhaite utiliser le GPU pour l'inférence
 * @property showMetrics Indique si les métriques de performance doivent être affichées
 * @property showConfidence Indique si les scores de confiance doivent être affichés
 * @property imageResolution Résolution d'image préférée pour l'inférence
 */
data class UserPreferences(
    val confidenceThreshold: Float = DEFAULT_CONFIDENCE_THRESHOLD,
    val useGPU: Boolean = DEFAULT_USE_GPU,
    val showMetrics: Boolean = DEFAULT_SHOW_METRICS,
    val showConfidence: Boolean = DEFAULT_SHOW_CONFIDENCE,
    val imageResolution: ImageResolution = DEFAULT_IMAGE_RESOLUTION
) {
    /**
     * Résolution d'image pour l'inférence.
     */
    enum class ImageResolution(val width: Int, val height: Int) {
        LOW(320, 320),
        MEDIUM(416, 416),
        HIGH(640, 640),
        ULTRA(1280, 1280)
    }
    
    companion object {
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.25f
        const val DEFAULT_USE_GPU = true
        const val DEFAULT_SHOW_METRICS = true
        const val DEFAULT_SHOW_CONFIDENCE = true
        val DEFAULT_IMAGE_RESOLUTION = ImageResolution.MEDIUM
    }
} 