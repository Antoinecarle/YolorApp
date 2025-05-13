package com.example.yolorapp.utils

import android.content.Context
import com.example.yolorapp.ml.ModelManager
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilitaire pour gérer les classes COCO.
 */
@Singleton
class CocoClassesUtils @Inject constructor(
    private val context: Context
) {
    // Cache des classes chargées
    private var classes: List<String>? = null
    
    /**
     * Charge les classes COCO depuis le fichier d'assets.
     * 
     * @return Liste des noms de classes COCO
     */
    fun loadCocoClasses(): List<String> {
        // Si déjà en cache, retourner directement
        classes?.let { return it }
        
        return try {
            val inputStream = context.assets.open(ModelManager.COCO_CLASSES_FILE)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val classList = reader.readLines()
            reader.close()
            
            // Mettre en cache pour les prochains appels
            classes = classList
            
            Timber.d("Classes COCO chargées avec succès: ${classList.size} classes")
            classList
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du chargement des classes COCO")
            // Liste par défaut vide en cas d'erreur
            emptyList()
        }
    }
    
    /**
     * Obtient le nom de la classe à partir de son identifiant.
     * 
     * @param classId Identifiant de la classe
     * @return Nom de la classe ou "Unknown" si non trouvé
     */
    fun getClassName(classId: Int): String {
        val classList = loadCocoClasses()
        return if (classId in classList.indices) {
            classList[classId]
        } else {
            Timber.w("Classe COCO introuvable pour l'ID: $classId")
            "Unknown"
        }
    }
} 