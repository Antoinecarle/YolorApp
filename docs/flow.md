# Flux Utilisateur et Parcours UX

## Parcours utilisateur principal

### 1. Lancement de l'application
- L'utilisateur ouvre l'application
- Écran de démarrage/splash screen (bref)
- Vérification des permissions (caméra, stockage)
- Si les permissions ne sont pas accordées, demande de permissions

### 2. Écran principal
- Interface avec prévisualisation caméra en direct
- Boutons de contrôle:
  - Capture d'image
  - Sélection d'image depuis la galerie
  - Paramètres (accès aux réglages)
  - Visualisation des derniers résultats

### 3. Flux de capture d'image
- L'utilisateur voit la prévisualisation caméra
- L'utilisateur cadre l'objet à détecter
- L'utilisateur appuie sur le bouton de capture
- L'image est capturée et envoyée au pipeline de détection
- Affichage d'un indicateur de chargement pendant l'inférence
- Affichage des résultats avec les détections

### 4. Flux de sélection depuis la galerie
- L'utilisateur appuie sur le bouton galerie
- Le sélecteur d'images du système s'ouvre
- L'utilisateur sélectionne une image
- L'image est chargée et envoyée au pipeline de détection
- Affichage d'un indicateur de chargement pendant l'inférence
- Affichage des résultats avec les détections

### 5. Écran de résultats
- Affichage de l'image avec les boîtes englobantes colorées
- Étiquettes des classes et scores de confiance
- Temps d'inférence affiché
- Options:
  - Partager le résultat
  - Retour à la caméra
  - Ajustement du seuil de confiance
  - Nouvelle détection

### 6. Écran de paramètres
- Réglage du seuil de confiance (slider)
- Option de basculement CPU/GPU (si disponible)
- Choix de la résolution d'image pour l'inférence
- Option d'affichage/masquage des scores de confiance
- Option d'affichage des métriques de performance

## Diagramme de flux logique

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Lancement App  │────▶│  Vérification   │────▶│  Écran Principal│
└─────────────────┘     │  Permissions    │     │  (Caméra)       │
                        └─────────────────┘     └───────┬─────────┘
                                                         │
                                                         ▼
                    ┌────────────────────────────────────┬────────────────────┐
                    │                                     │                    │
                    ▼                                     ▼                    ▼
        ┌─────────────────────┐              ┌─────────────────┐    ┌─────────────────┐
        │ Capture Image       │              │ Sélection Image │    │ Paramètres      │
        │ depuis Caméra       │              │ depuis Galerie  │    │                 │
        └──────────┬──────────┘              └────────┬────────┘    └─────────────────┘
                   │                                   │
                   ▼                                   ▼
        ┌─────────────────────┐              ┌─────────────────┐
        │ Traitement Image &  │◄─────────────┤ Chargement      │
        │ Inférence YOLOR     │              │ Image           │
        └──────────┬──────────┘              └─────────────────┘
                   │
                   ▼
        ┌─────────────────────┐              ┌─────────────────┐
        │ Affichage Résultats │──────────────▶ Partage         │
        │ avec Détections     │              │ Résultats       │
        └──────────┬──────────┘              └─────────────────┘
                   │
                   ▼
        ┌─────────────────────┐
        │ Retour à la Caméra  │
        │ ou Nouvelle Analyse │
        └─────────────────────┘
```

## Interactions API

### API Caméra Android
- Utilisation de CameraX pour la capture d'images
- Prévisualisation en direct avec CameraPreview
- Capture d'image avec ImageCapture
- Conversion au format Bitmap pour l'inférence

### API de reconnaissance d'image
- Conversion de l'image en format compatible avec le modèle TFLite/ONNX
- Appel à l'interpréteur TensorFlow Lite ou exécuteur ONNX
- Décodage des résultats bruts en détections (boîtes, classes, scores)
- Application de Non-Maximum Suppression pour filtrer les doublons

### Cycle de vie des vues
- MainActivity: Point d'entrée principal, gestion des permissions
- CameraFragment: Gestion de la caméra et prévisualisation
- ResultFragment: Affichage des résultats de détection
- SettingsFragment: Paramètres de l'application

### Stockage et partage
- Sauvegarde temporaire des images capturées
- Sauvegarde des résultats de détection pour partage
- Utilisation de FileProvider pour le partage d'images avec détections 