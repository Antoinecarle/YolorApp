package com.example.yolorapp.data.model

import android.graphics.RectF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Représente une détection d'objet individuelle.
 *
 * @property classId Identifiant de la classe de l'objet détecté
 * @property className Nom de la classe de l'objet détecté
 * @property confidence Score de confiance [0,1] pour cette détection
 * @property boundingBox Rectangle contenant l'objet détecté (coordonnées absolues)
 */
@Parcelize
data class Detection(
    val classId: Int,
    val className: String,
    val confidence: Float,
    val boundingBox: RectF
) : Parcelable 