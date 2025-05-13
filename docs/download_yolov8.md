# Téléchargement et intégration du modèle YOLOv8 pour YolorApp

Ce document explique comment télécharger et intégrer un modèle YOLOv8 depuis Hugging Face pour l'utiliser dans l'application YolorApp.

## Méthode avec PyTorch Mobile

Nous avons opté pour l'utilisation directe du modèle PyTorch (.pt) via PyTorch Mobile, car la conversion vers TensorFlow Lite présentait des problèmes de compatibilité.

### Prérequis
- Accès à Internet pour télécharger le modèle
- Un navigateur web

### Étapes

1. **Télécharger le modèle YOLOv8n depuis Hugging Face**

   Rendez-vous sur le dépôt Hugging Face de YOLOv8 :
   [https://huggingface.co/Ultralytics/YOLOv8](https://huggingface.co/Ultralytics/YOLOv8)

   Téléchargez le fichier `yolov8n.pt` en cliquant dessus.

2. **Placer le modèle dans le projet Android**

   Copiez le fichier `yolov8n.pt` téléchargé dans le dossier `app/src/main/assets/` de votre projet Android.

   ```bash
   # Depuis la racine du projet
   cp /chemin/vers/yolov8n.pt app/src/main/assets/
   ```

3. **Vérifier la présence du modèle**

   Assurez-vous que le fichier `yolov8n.pt` est bien présent dans le dossier assets :

   ```bash
   ls -la app/src/main/assets
   ```

## Configuration de l'application

L'application est maintenant configurée pour utiliser PyTorch Mobile au lieu de TensorFlow Lite :

1. **Dépendances ajoutées dans build.gradle**

   ```gradle
   // PyTorch Mobile
   implementation 'org.pytorch:pytorch_android_lite:1.13.1'
   implementation 'org.pytorch:pytorch_android_torchvision_lite:1.13.1'
   ```

2. **ModelManager.kt configuré pour utiliser le modèle PyTorch**

   ```kotlin
   companion object {
       const val DEFAULT_MODEL_NAME = "yolov8n.pt"
       const val COCO_CLASSES_FILE = "coco_classes.txt"
   }
   ```

3. **YolorInferenceEngine adapté pour utiliser PyTorch Mobile**

   Le moteur d'inférence a été modifié pour utiliser les classes `Module`, `IValue` et `TensorImageUtils` de PyTorch Mobile.

## Variantes de modèles YOLOv8

YOLOv8 propose plusieurs variantes selon vos besoins de performance :

| Modèle     | Taille (MB) | Précision | Vitesse |
|------------|-------------|-----------|---------|
| YOLOv8n    | 6.7         | Basique   | Plus rapide |
| YOLOv8s    | 22.4        | Moyenne   | Rapide |
| YOLOv8m    | 52.2        | Bonne     | Équilibré |
| YOLOv8l    | 86.7        | Très bonne | Plus lent |
| YOLOv8x    | 130.7       | Excellente | Lent |

Pour les appareils mobiles, les modèles `n` ou `s` sont recommandés pour un bon équilibre entre précision et vitesse.

## Avantages de l'approche PyTorch Mobile

- Pas besoin de conversion du modèle (risque d'erreurs réduit)
- Implémentation plus simple et directe
- Support natif du format PyTorch (.pt)
- Meilleures performances potentielles par rapport aux modèles convertis
- Maintenance plus facile lors des mises à jour des modèles 