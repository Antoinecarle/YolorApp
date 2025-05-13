# Exigences du Projet YOLOR pour Android

## Exigences fonctionnelles

### Interface utilisateur
- **F1**: L'application doit fournir une interface caméra pour la capture d'images en temps réel
- **F2**: L'application doit permettre de sélectionner des images depuis la galerie
- **F3**: L'application doit afficher les résultats de détection avec des boîtes englobantes colorées
- **F4**: L'application doit afficher le nom de la classe et le score de confiance pour chaque objet détecté
- **F5**: L'application doit permettre d'ajuster le seuil de confiance via un curseur
- **F6**: L'application doit afficher les métriques de performance (temps d'inférence)

### Détection d'objets
- **F7**: L'application doit charger correctement le modèle YOLOR optimisé
- **F8**: L'application doit effectuer la détection d'objets sur les images capturées ou sélectionnées
- **F9**: L'application doit supporter la détection multi-classes
- **F10**: L'application doit maintenir une liste des 80 classes COCO standard

### Performances
- **F11**: L'application doit afficher les temps d'inférence pour chaque image
- **F12**: L'application doit permettre de basculer entre CPU et GPU (si disponible)
- **F13**: L'application doit fonctionner sans crasher sur le Pixel 8 cible

## Exigences techniques

### Développement
- **T1**: Le projet doit utiliser Android Studio comme environnement de développement
- **T2**: Le code doit être écrit en Kotlin et/ou Java
- **T3**: L'application doit cibler Android API 33+ (Android 13 ou supérieur)
- **T4**: Le code doit suivre les bonnes pratiques Android et les principes SOLID

### Modèle ML
- **T5**: Le modèle YOLOR doit être converti en format TensorFlow Lite (.tflite) ou ONNX
- **T6**: La taille du modèle converti ne doit pas dépasser 50 Mo
- **T7**: Le modèle doit être optimisé pour les performances mobiles (quantification, pruning si nécessaire)
- **T8**: Le pipeline de post-traitement doit gérer correctement le décodage des prédictions YOLOR

### Performances
- **T9**: Le temps d'inférence sur Pixel 8 doit être inférieur à 500ms par image
- **T10**: La consommation de mémoire ne doit pas dépasser 300 Mo pendant l'inférence
- **T11**: L'application doit maintenir une température acceptable de l'appareil lors d'une utilisation prolongée

### Interface
- **T12**: L'interface utilisateur doit suivre les principes Material Design
- **T13**: L'application doit s'adapter aux différentes tailles d'écran et orientations
- **T14**: L'application doit demander et gérer correctement les permissions de caméra et de stockage

## Contraintes
- Le modèle doit être suffisamment optimisé pour fonctionner sur du matériel mobile
- L'application doit être testée et optimisée spécifiquement pour le Pixel 8
- Les performances et la précision doivent être équilibrées pour une utilisation pratique 