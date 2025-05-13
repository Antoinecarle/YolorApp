package com.example.yolorapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Contient les résultats complets d'une session de détection.
 *
 * @property detections Liste des objets détectés
 * @property inferenceTime Temps d'inférence en millisecondes
 * @property imageWidth Largeur de l'image analysée
 * @property imageHeight Hauteur de l'image analysée
 * @property confidenceThreshold Seuil de confiance utilisé pour cette détection
 */
@Parcelize
data class DetectionResults(
    val detections: List<Detection>,
    val inferenceTime: Long, // en ms
    val imageWidth: Int,
    val imageHeight: Int,
    val confidenceThreshold: Float
) : Parcelable 