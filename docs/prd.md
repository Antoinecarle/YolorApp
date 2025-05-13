# Spécification Produit (PRD) - Application YOLOR pour Android

## Vue d'ensemble
L'application YOLOR pour Android est une preuve de concept visant à démontrer les capacités du modèle de détection d'objets YOLOR sur un appareil mobile, en particulier sur un Pixel 8. L'application utilise la caméra de l'appareil pour capturer des images en temps réel et effectuer une détection d'objets à l'aide du modèle YOLOR optimisé.

## Public cible
- Chercheurs et développeurs en vision par ordinateur
- Testeurs de performances de modèles ML sur appareils mobiles
- Développeurs d'applications de reconnaissance d'image

## Fonctionnalités principales

### 1. Capture d'images en temps réel
- Interface caméra intuitive
- Prévisualisation en direct
- Option pour utiliser des images de la galerie

### 2. Détection d'objets avec YOLOR
- Chargement du modèle YOLOR optimisé pour mobile
- Inférence sur les images capturées ou sélectionnées
- Détection multi-classes (selon les classes sur lesquelles le modèle a été entraîné)

### 3. Visualisation des résultats
- Affichage des boîtes englobantes autour des objets détectés
- Affichage des étiquettes de classe et scores de confiance
- Option pour ajuster le seuil de confiance

### 4. Mesure des performances
- Temps d'inférence par image
- Utilisation du CPU/GPU
- Consommation de mémoire

## Spécifications techniques

### Configuration matérielle cible
- Appareil: Google Pixel 8
- Processeur: Google Tensor G3
- RAM: 8 Go
- OS: Android 14 ou supérieur

### Format du modèle
- Format de conversion: TensorFlow Lite (tflite) ou ONNX
- Taille de modèle cible: < 50 Mo pour assurer des performances optimales

### Performances attendues
- Temps d'inférence: < 500ms par image
- FPS: > 5 pour une détection en temps réel fluide
- Précision: Comparable à 80-90% des performances du modèle original

## Futur développement (hors champ initial)
- Support pour d'autres architectures YOLO
- Mode vidéo en continu
- Exportation des résultats de détection
- Personnalisation des modèles 