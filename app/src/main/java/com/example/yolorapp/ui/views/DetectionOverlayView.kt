package com.example.yolorapp.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.yolorapp.R
import com.example.yolorapp.data.model.Detection
import com.example.yolorapp.data.model.DetectionResults
import timber.log.Timber

/**
 * Vue personnalisée pour afficher les détections par dessus une image.
 */
class DetectionOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    // Résultats de détection à afficher
    private var detectionResults: DetectionResults? = null
    
    // Dimensions de l'image source
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    
    // Flag pour afficher les scores de confiance
    private var showConfidence: Boolean = true
    
    // Échelle de mise à l'échelle entre l'image source et la vue
    private var scaleX: Float = 1.0f
    private var scaleY: Float = 1.0f
    
    // Couleurs pour les boîtes (sera généré dynamiquement)
    private val colorMap = mutableMapOf<Int, Int>()
    
    // Paints pour le dessin
    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = context.getColor(R.color.detection_box_stroke)
    }
    
    private val textBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.detection_text_background)
    }
    
    private val textPaint = Paint().apply {
        color = context.getColor(R.color.detection_text_color)
        textSize = 36f
        isFakeBoldText = true
    }
    
    /**
     * Met à jour les résultats de détection à afficher.
     */
    fun setDetectionResults(results: DetectionResults, showConfidence: Boolean = true) {
        this.detectionResults = results
        this.imageWidth = results.imageWidth
        this.imageHeight = results.imageHeight
        this.showConfidence = showConfidence
        
        // Générer des couleurs pour chaque classe si nécessaire
        results.detections.forEach { detection ->
            if (!colorMap.containsKey(detection.classId)) {
                colorMap[detection.classId] = generateColorForClass(detection.classId)
            }
        }
        
        // Demander un rafraîchissement de la vue
        invalidate()
    }
    
    /**
     * Met à jour l'affichage des scores de confiance.
     */
    fun setShowConfidence(showConfidence: Boolean) {
        this.showConfidence = showConfidence
        invalidate()
    }
    
    /**
     * Efface les résultats de détection.
     */
    fun clearDetections() {
        detectionResults = null
        invalidate()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculer les facteurs d'échelle entre l'image source et la vue
        if (imageWidth > 0 && imageHeight > 0) {
            scaleX = w.toFloat() / imageWidth
            scaleY = h.toFloat() / imageHeight
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val results = detectionResults ?: return
        
        // Mettre à jour les facteurs d'échelle
        scaleX = width.toFloat() / imageWidth
        scaleY = height.toFloat() / imageHeight
        
        // Dessiner chaque détection
        for (detection in results.detections) {
            drawDetection(canvas, detection)
        }
    }
    
    /**
     * Dessine une détection individuelle sur le canvas.
     */
    private fun drawDetection(canvas: Canvas, detection: Detection) {
        // Mettre à l'échelle la boîte englobante
        val scaledBox = RectF(
            detection.boundingBox.left * scaleX,
            detection.boundingBox.top * scaleY,
            detection.boundingBox.right * scaleX,
            detection.boundingBox.bottom * scaleY
        )
        
        // Obtenir la couleur pour cette classe
        val color = colorMap[detection.classId] ?: Color.YELLOW
        boxPaint.color = color
        
        // Dessiner la boîte englobante
        canvas.drawRect(scaledBox, boxPaint)
        
        // Préparer le texte à afficher
        val label = if (showConfidence) {
            "${detection.className} (${(detection.confidence * 100).toInt()}%)"
        } else {
            detection.className
        }
        
        // Mesurer la taille du texte
        val textBounds = Rect()
        textPaint.getTextBounds(label, 0, label.length, textBounds)
        
        // Dessiner le fond du texte
        val textBackgroundRect = RectF(
            scaledBox.left,
            scaledBox.top - textBounds.height() - 16,
            scaledBox.left + textBounds.width() + 16,
            scaledBox.top
        )
        canvas.drawRect(textBackgroundRect, textBackgroundPaint)
        
        // Dessiner le texte
        canvas.drawText(
            label,
            scaledBox.left + 8,
            scaledBox.top - 8,
            textPaint
        )
    }
    
    /**
     * Génère une couleur unique pour une classe donnée.
     */
    private fun generateColorForClass(classId: Int): Int {
        // Liste de couleurs prédéfinies vives pour une meilleure distinction
        val colors = arrayOf(
            Color.parseColor("#FF5722"), // Deep Orange
            Color.parseColor("#E91E63"), // Pink
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#673AB7"), // Deep Purple
            Color.parseColor("#3F51B5"), // Indigo
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#03A9F4"), // Light Blue
            Color.parseColor("#00BCD4"), // Cyan
            Color.parseColor("#009688"), // Teal
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#8BC34A"), // Light Green
            Color.parseColor("#CDDC39"), // Lime
            Color.parseColor("#FFEB3B"), // Yellow
            Color.parseColor("#FFC107"), // Amber
            Color.parseColor("#FF9800")  // Orange
        )
        
        return colors[classId % colors.size]
    }
} 