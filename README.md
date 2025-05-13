# YolorApp - Application Android de détection d'objets avec YOLOv8

YolorApp est une application Android qui utilise le modèle YOLOv8 (You Only Look Once version 8) pour la détection d'objets en temps réel. Cette application propose différentes fonctionnalités telles que la détection via la caméra, l'analyse d'images depuis la galerie, et la visualisation des résultats de détection.

## Configuration requise

- Android Studio Arctic Fox (2020.3.1) ou version plus récente
- Appareil Android ou émulateur avec Android 8.0 (API 26) ou version plus récente
- Un appareil avec un appareil photo pour utiliser les fonctionnalités de détection en temps réel

## Installation

1. Clonez ce dépôt sur votre machine locale :
   ```
   git clone https://github.com/votre-utilisateur/yolorApp.git
   ```

2. Ouvrez le projet dans Android Studio

3. **Important**: Téléchargez le modèle YOLOv8 requis et placez-le dans le dossier des assets :
   - Le modèle `yolov8n.tflite` doit être placé dans `app/src/main/assets/`
   - Consultez le document [docs/download_yolov8.md](docs/download_yolov8.md) pour obtenir des instructions sur la façon de télécharger et convertir ce modèle

4. Compilez et exécutez l'application sur votre appareil ou émulateur

## Obtenir le modèle YOLOv8

L'application nécessite un modèle YOLOv8 au format TensorFlow Lite. Voici comment l'obtenir :

### Option 1: Utiliser le script de conversion fourni

Nous fournissons un script Python simple pour télécharger et convertir le modèle YOLOv8 :

```bash
# Installer les dépendances
pip install ultralytics

# Exécuter le script de conversion
python convert_yolov8.py
```

Le script téléchargera automatiquement le modèle YOLOv8n et le convertira en format TensorFlow Lite.

### Option 2: Conversion manuelle

Vous pouvez également effectuer la conversion manuellement :

```python
from ultralytics import YOLO

# Télécharger le modèle
model = YOLO("yolov8n.pt")  # ou "yolov8s.pt" pour un modèle plus grand

# Exporter en TFLite
model.export(format="tflite")
```

### Option 3: Choisir une variante de YOLOv8

YOLOv8 propose plusieurs variantes selon vos besoins de performance :

| Modèle   | Taille (MB) | Précision | Vitesse      |
|----------|-------------|-----------|--------------|
| YOLOv8n  | 6.7         | Basique   | Plus rapide  |
| YOLOv8s  | 22.4        | Moyenne   | Rapide       |
| YOLOv8m  | 52.2        | Bonne     | Équilibré    |
| YOLOv8l  | 86.7        | Très bonne| Plus lent    |
| YOLOv8x  | 130.7       | Excellente| Lent         |

Pour les appareils mobiles, `yolov8n.tflite` ou `yolov8s.tflite` sont recommandés.

## Utilisation de l'application

L'application propose différentes fonctionnalités :

### Détection avec la caméra
- Lancez l'application
- Sélectionnez l'option "Caméra" 
- Pointez la caméra vers les objets à détecter
- Les boîtes de détection apparaîtront autour des objets identifiés

### Détection avec des images de la galerie
- Lancez l'application
- Sélectionnez l'option "Galerie"
- Choisissez une image depuis votre galerie
- Les résultats de détection seront affichés sur l'image

### Configuration des paramètres
- Dans l'écran principal, accédez aux paramètres
- Ajustez le seuil de confiance pour la détection
- Choisissez la résolution d'image souhaitée
- Activez ou désactivez l'accélération GPU (si disponible)

## Architecture du projet

L'application est basée sur l'architecture MVVM (Model-View-ViewModel) et utilise les composants Android modernes :

- **Hilt** pour l'injection de dépendances
- **CameraX** pour la gestion de la caméra
- **TensorFlow Lite** pour l'inférence du modèle
- **Coroutines & Flow** pour les opérations asynchrones
- **Navigation Component** pour la navigation entre les fragments

## Licence

Ce projet est sous licence [Licence Publique] - voir le fichier LICENSE pour plus de détails.

## Remerciements

- [YOLOR](https://github.com/WongKinYiu/yolor) pour le modèle de détection d'objets
- [TensorFlow Lite](https://www.tensorflow.org/lite) pour le framework d'inférence mobile

## Documentation

La documentation complète du projet est disponible dans le dossier `/docs`:

- [Contexte du projet](docs/context.md)
- [Spécification produit](docs/prd.md)
- [Exigences fonctionnelles et techniques](docs/requirements.md)
- [Flux utilisateur et parcours UX](docs/flow.md)
- [Architecture frontend](docs/frontend.md)
- [Architecture backend et traitement d'image](docs/backend.md)
- [État d'avancement](docs/status.md)
- [Stack technologique](docs/techstack.md)
- [Journal des décisions](docs/decisions.md)
- [Notes et réflexions](docs/notes.md)

## Prérequis

- Android Studio Iguana (2023.2.1) ou supérieur
- Android SDK 34
- Java JDK 17
- Un appareil Android 8.0+ (API 26+)
- Pixel 8 recommandé pour les tests de performance

## Mise en route

1. Cloner ce dépôt
   ```bash
   git clone https://github.com/votre-username/yolorApp.git
   ```

2. Ouvrir le projet dans Android Studio

3. Synchroniser avec Gradle

4. Exécuter l'application sur un appareil physique (émulateur non recommandé pour les tests de performance)

## Crédits et références

- [YOLOv8 par Ultralytics](https://github.com/ultralytics/ultralytics)
- [YOLOv8 sur Hugging Face](https://huggingface.co/Ultralytics/YOLOv8)
- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [CameraX Android](https://developer.android.com/training/camerax)

## Licence

Ce projet est sous licence [MIT](LICENSE).

## Contact

Pour toute question ou suggestion concernant ce projet, veuillez ouvrir une issue sur ce dépôt. 