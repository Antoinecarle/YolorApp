# Stack Technologique

## Environnement de développement
- **IDE**: Android Studio Iguana | 2023.2.1
- **Système de build**: Gradle 8.2+
- **SDK Android**: 
  - SDK Minimum: API 26 (Android 8.0)
  - SDK Cible: API 34 (Android 14)
- **Système de contrôle de version**: Git

## Frontend (Android)
- **Langage**: Kotlin 1.9+
- **Architecture**: MVVM (Model-View-ViewModel)
- **Composants UI**:
  - AndroidX AppCompat
  - Material Design Components 1.11.0
  - ConstraintLayout 2.1.4
  - RecyclerView, CardView
  - ViewPager2
  - Fragment et Navigation Component
- **Liaison de données**:
  - DataBinding
  - ViewBinding
- **Gestion du cycle de vie**:
  - ViewModel
  - LiveData / StateFlow
  - Lifecycle-aware components
- **Interface caméra**:
  - CameraX 1.3.0

## Backend (Traitement d'image et Inférence)
- **Framework ML**:
  - TensorFlow Lite 2.14.0 (principal)
  - ONNX Runtime 1.15.0 (alternative)
- **Multithreading**:
  - Kotlin Coroutines
  - Flow
- **Formats de modèle**:
  - TFLite (.tflite) - Format principal
  - ONNX (.onnx) - Format alternatif
- **Traitement d'image**:
  - AndroidX Core-KTX
  - AndroidX ExifInterface
  - Bitmap manipulation APIs

## Stockage et données
- **Préférences**:
  - Jetpack DataStore / SharedPreferences
- **Base de données** (pour métriques de performance):
  - Room Database 2.6.0
- **Sérialisation**:
  - Kotlin Serialization
  - JSON (org.json ou Gson)

## Injection de dépendances
- **Framework**:
  - Hilt 2.48.0 (basé sur Dagger)
  - Ou Koin 3.5.0 (plus léger)

## Test
- **Test unitaire**:
  - JUnit 5
  - Mockito / MockK
  - Turbine (pour les Flow)
- **Test d'instrumentation**:
  - Espresso
  - AndroidX Test
- **Couverture de code**:
  - JaCoCo

## Monitoring et analytics
- **Journalisation**:
  - Timber
- **Surveillance des performances**:
  - Custom metrics logging
  - Profiler Android Studio

## Modèle ML
- **Framework d'origine**: PyTorch (YOLOR original)
- **Format d'origine**: PyTorch (.pt)
- **Outils de conversion**:
  - ONNX Export (PyTorch → ONNX)
  - TensorFlow Lite Converter (ONNX → TFLite)
- **Optimisations**:
  - Quantification (INT8)
  - Pruning
  - Optimisation pour mobile

## Bibliothèques externes
- **Gestion des images**:
  - Glide 4.15.0 / Coil 2.4.0
- **Gestion des permissions**:
  - EasyPermissions 3.0.0 ou Permissions Dispatcher
- **Utilitaires**:
  - Android KTX
  - ThreeTenABP (gestion des dates)

## Versions des dépendances majeures

| Dépendance | Version | Description |
|------------|---------|-------------|
| Kotlin | 1.9.20 | Langage de programmation |
| Coroutines | 1.7.3 | Programmation asynchrone |
| AndroidX Core | 1.12.0 | Fonctionnalités de base |
| AndroidX AppCompat | 1.6.1 | Compatibilité descendante |
| Material Design | 1.11.0 | Composants UI Material |
| Navigation | 2.7.5 | Gestion de la navigation |
| CameraX | 1.3.0 | API de la caméra |
| Lifecycle | 2.6.2 | Gestion du cycle de vie |
| TensorFlow Lite | 2.14.0 | Inférence ML |
| ONNX Runtime | 1.15.0 | Alternative pour ML |
| Room | 2.6.0 | Base de données locale |
| Hilt | 2.48.0 | Injection de dépendances |

## Compatibilité matérielle et logicielle
- **Appareils cibles**:
  - Google Pixel 8 (principal)
  - Autres appareils Android modernes (secondaire)
- **Hardware requis**:
  - CPU: ARM64 (aarch64)
  - RAM: 6 Go minimum recommandé
  - Stockage: 100 Mo minimum
  - Caméra: Requise
- **Options d'accélération**:
  - GPU (via délégué TFLite GPU)
  - NNAPI (Android Neural Networks API)
  - Hexagon DSP (appareils qualcomm)

## Outils de build et déploiement
- **Build System**: Gradle 8.2.0+
- **Kotlin Compiler**: JVM target 17
- **Packaging**: Android App Bundle (.aab) / APK (.apk)
- **ProGuard / R8**: Activé pour la minimisation et l'obfuscation 