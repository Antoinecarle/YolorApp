package com.example.yolorapp.ml

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.SystemClock
import androidx.core.net.toFile
import com.example.yolorapp.data.model.UserPreferences
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classe de prétraitement des images pour préparer les entrées pour le modèle YOLOR.
 */
@Singleton
class ImageProcessor @Inject constructor() {
    
    /**
     * Prétraite une image Bitmap pour l'inférence YOLOR.
     * 
     * @param bitmap Image d'entrée
     * @param inputSize Taille d'entrée attendue par le modèle
     * @return FloatBuffer contenant l'image prétraitée au format [1, 3, height, width]
     */
    fun preprocess(bitmap: Bitmap, inputSize: Int): FloatBuffer {
        val startTime = SystemClock.elapsedRealtime()
        
        // Redimensionner l'image à la taille d'entrée attendue par le modèle
        val scaledBitmap = if (bitmap.width != inputSize || bitmap.height != inputSize) {
            Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        } else {
            bitmap
        }
        
        // Créer un buffer pour stocker les données de l'image
        // Format NCHW: [batch_size(1), channels(3), height, width]
        val buffer = FloatBuffer.allocate(1 * 3 * inputSize * inputSize)
        
        // Normalisation et réorganisation des canaux (RGB to NCHW)
        for (c in 0 until 3) { // For each channel (RGB)
            for (y in 0 until inputSize) {
                for (x in 0 until inputSize) {
                    val pixel = scaledBitmap.getPixel(x, y)
                    // Normalisation dans [0,1]
                    val value = when (c) {
                        0 -> (pixel shr 16 and 0xFF) / 255.0f // R
                        1 -> (pixel shr 8 and 0xFF) / 255.0f  // G
                        else -> (pixel and 0xFF) / 255.0f     // B
                    }
                    buffer.put(value)
                }
            }
        }
        
        // Remettre le buffer au début
        buffer.rewind()
        
        val preprocessingTime = SystemClock.elapsedRealtime() - startTime
        Timber.d("Prétraitement d'image terminé en $preprocessingTime ms")
        
        // Libérer la mémoire si une nouvelle bitmap a été créée
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        
        return buffer
    }
    
    /**
     * Prétraite une image pour l'inférence en fonction de la résolution préférée.
     * 
     * @param bitmap Image source
     * @param resolution Résolution préférée pour l'inférence
     * @return FloatBuffer contenant l'image prétraitée
     */
    fun preprocessForInference(bitmap: Bitmap, resolution: UserPreferences.ImageResolution): FloatBuffer {
        return preprocess(bitmap, resolution.width)
    }
    
    /**
     * Convertit un ByteBuffer en tableau multidimensionnel pour l'inférence.
     * 
     * @param buffer Buffer d'entrée au format NCHW
     * @param batchSize Taille du lot
     * @param channels Nombre de canaux (3 pour RGB)
     * @param height Hauteur de l'image
     * @param width Largeur de l'image
     * @return Array<Array<Array<FloatArray>>> au format [batch_size, channels, height, width]
     */
    fun convertBufferToArray(
        buffer: FloatBuffer,
        batchSize: Int,
        channels: Int,
        height: Int,
        width: Int
    ): Array<Array<Array<FloatArray>>> {
        buffer.rewind()
        
        return Array(batchSize) { b ->
            Array(channels) { c ->
                Array(height) { h ->
                    FloatArray(width) { w ->
                        val index = b * channels * height * width +
                                c * height * width +
                                h * width +
                                w
                        buffer.get(index)
                    }
                }
            }
        }
    }
    
    /**
     * Corrige la rotation de l'image en fonction des métadonnées EXIF.
     * 
     * @param bitmap Bitmap à corriger
     * @param uri Uri de l'image
     * @return Bitmap avec rotation corrigée
     */
    fun fixImageRotation(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val file = uri.toFile()
            val exif = ExifInterface(FileInputStream(file))
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
                else -> return bitmap
            }
            
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors de la correction de la rotation de l'image")
            return bitmap
        }
    }
} 