# Architecture Frontend

## Structure de l'interface utilisateur

### Écrans principaux
1. **SplashScreen** - Écran de démarrage avec logo et animation
2. **MainActivity** - Activité principale et conteneur des fragments
3. **CameraFragment** - Fragment pour la capture d'image et prévisualisation caméra
4. **ResultFragment** - Fragment pour l'affichage des résultats de détection
5. **SettingsFragment** - Fragment pour les paramètres de l'application
6. **GalleryFragment** - Fragment pour la sélection d'images depuis la galerie

### Composants UI

#### Layout principal (activity_main.xml)
- FrameLayout - Conteneur pour les fragments
- BottomNavigationView - Navigation entre les écrans principaux (Caméra, Galerie, Paramètres)

#### Layout Caméra (fragment_camera.xml)
- PreviewView - Prévisualisation du flux caméra
- FloatingActionButton - Bouton de capture d'image
- ImageButton - Accès à la galerie
- ImageButton - Accès aux paramètres
- CircularProgressIndicator - Indicateur de chargement pendant l'inférence

#### Layout Résultats (fragment_result.xml)
- ImageView - Affichage de l'image avec détections
- RecyclerView - Liste des objets détectés avec classe et confiance
- TextView - Temps d'inférence et autres métriques
- Button - Retour à la caméra
- Button - Partage des résultats
- Slider - Ajustement du seuil de confiance en direct

#### Layout Paramètres (fragment_settings.xml)
- Slider - Réglage du seuil de confiance par défaut
- Switch - Activation/désactivation du GPU
- RadioGroup - Sélection de la résolution d'inférence
- Switch - Affichage/masquage des scores de confiance
- Switch - Affichage/masquage des métriques de performance

## Gestion de l'état

### Architecture MVVM
- **Modèles** - Classes de données pour les détections, paramètres, etc.
- **Vues** - Activités et fragments pour l'UI
- **ViewModels** - Logique de présentation et gestion de l'état

### DataBinding
- Liaison entre les vues XML et les données du ViewModel
- Mise à jour automatique de l'UI lors des changements d'état

### Navigation
- Utilisation du composant Navigation d'Android Jetpack
- Navigation entre fragments via NavController
- Transitions animées entre les écrans

## Composants graphiques personnalisés

### ObjectDetectionOverlay
- Vue personnalisée pour dessiner les boîtes englobantes
- Superposition sur l'image de résultat
- Dessine les rectangles colorés, étiquettes et scores

### ConfidenceSlider
- Composant personnalisé pour l'ajustement du seuil de confiance
- Affichage visuel du nombre d'objets détectés à différents seuils
- Mise à jour en temps réel des détections affichées

### PerformanceMetricsView
- Vue personnalisée pour l'affichage des métriques de performance
- Graphiques pour le temps d'inférence
- Indicateurs pour l'utilisation CPU/GPU et mémoire

## Thème et style

### Palette de couleurs
- Couleur primaire: #1E88E5 (Bleu)
- Couleur secondaire: #43A047 (Vert)
- Couleur d'accent: #FFC107 (Jaune)
- Couleurs de fond: #FFFFFF (clair) / #121212 (sombre)

### Support du thème sombre
- Utilisation de ?attr/colorPrimary pour compatibilité
- Ressources alternatives pour le mode sombre
- Adaptation automatique au thème système

### Composants Material Design
- Utilisation des composants Material Design (Material Components for Android)
- Boutons, cartes, sliders, switches conformes aux spécifications Material
- Typographie et espacement cohérents

## Responsive design
- Support des différentes tailles d'écran et densités
- Layouts alternatifs pour orientation portrait/paysage
- Utilisation de ConstraintLayout pour adaptation flexible 