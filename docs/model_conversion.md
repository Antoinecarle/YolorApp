# Guide de conversion du modèle YOLOR vers TensorFlow Lite

Ce document explique comment convertir un modèle YOLOR PyTorch vers le format TensorFlow Lite pour une utilisation dans l'application YolorApp.

## Prérequis

- Python 3.8 ou supérieur
- PyTorch (1.9.0 ou supérieur)
- TensorFlow (2.5.0 ou supérieur)
- ONNX (1.9.0 ou supérieur)
- onnx-tf (conversion ONNX vers TensorFlow)

Vous pouvez installer les dépendances nécessaires avec la commande suivante :

```bash
pip install torch tensorflow onnx onnx-tf
```

## Étapes de conversion

### 1. Télécharger le modèle YOLOR pré-entraîné

Téléchargez le modèle YOLOR-P6 à partir du dépôt officiel :

```bash
# Créer un dossier pour stocker les modèles
mkdir -p models
cd models

# Télécharger le modèle YOLOR-P6
wget https://github.com/WongKinYiu/yolor/releases/download/weights/yolor-p6.pt
```

Si vous préférez, vous pouvez télécharger manuellement le modèle depuis [cette page](https://github.com/WongKinYiu/yolor/releases/tag/weights).

### 2. Convertir de PyTorch vers ONNX

Créez un script Python pour convertir le modèle PyTorch en format ONNX :

```python
import torch
import torch.nn as nn
import sys
import onnx
import onnxsim

# Chemin vers le modèle PyTorch
pt_model_path = 'yolor-p6.pt'
# Chemin de sortie pour le modèle ONNX
onnx_model_path = 'yolor_p6_640.onnx'

# Charger le modèle PyTorch
print(f'Chargement du modèle {pt_model_path}...')
model = torch.load(pt_model_path, map_location='cpu')
if 'model' in model:
    model = model['model']  # Extraire le modèle si c'est un checkpoint
model = model.float().eval()  # Convertir en float et passer en mode évaluation

# Préparer un tenseur d'entrée factice (taille 640x640)
input_tensor = torch.randn(1, 3, 640, 640)

# Exporter vers ONNX
print(f'Exportation vers ONNX format...')
torch.onnx.export(
    model,
    input_tensor,
    onnx_model_path,
    verbose=False,
    opset_version=12,
    input_names=['input'],
    output_names=['output'],
    dynamic_axes={
        'input': {0: 'batch_size'},
        'output': {0: 'batch_size'}
    }
)

# Simplifier le modèle ONNX (optionnel mais recommandé)
print(f'Simplification du modèle ONNX...')
onnx_model = onnx.load(onnx_model_path)
model_simplifié, vérification = onnxsim.simplify(onnx_model)
if vérification:
    onnx.save(model_simplifié, onnx_model_path)
    print(f'Modèle simplifié sauvegardé: {onnx_model_path}')
else:
    print('La simplification a échoué')

print('Conversion PyTorch → ONNX terminée.')
```

### 3. Convertir de ONNX vers TensorFlow

Utilisez onnx-tf pour convertir le modèle ONNX en modèle TensorFlow :

```python
import onnx
from onnx_tf.backend import prepare

# Charger le modèle ONNX
onnx_model = onnx.load('yolor_p6_640.onnx')

# Convertir en TensorFlow
tf_rep = prepare(onnx_model)

# Sauvegarder le modèle TensorFlow
tf_rep.export_graph('yolor_p6_640_tf')

print('Conversion ONNX → TensorFlow terminée.')
```

### 4. Convertir de TensorFlow vers TensorFlow Lite

Enfin, convertissez le modèle TensorFlow en format TensorFlow Lite :

```python
import tensorflow as tf

# Chemin vers le répertoire du modèle TensorFlow
tf_model_dir = 'yolor_p6_640_tf'
# Chemin de sortie pour le modèle TFLite
tflite_model_path = 'yolor_p6_640.tflite'

# Convertir en TFLite
converter = tf.lite.TFLiteConverter.from_saved_model(tf_model_dir)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.target_spec.supported_types = [tf.float16]  # FP16 quantization
tflite_model = converter.convert()

# Sauvegarder le modèle TFLite
with open(tflite_model_path, 'wb') as f:
    f.write(tflite_model)

print(f'Conversion TensorFlow → TFLite terminée. Modèle sauvegardé: {tflite_model_path}')
```

## Méthode alternative : utiliser YOLOR-TFLite

Une alternative est d'utiliser le dépôt [tensorflow-yolov4-tflite](https://github.com/hunglc007/tensorflow-yolov4-tflite) qui supporte la conversion des modèles YOLO (y compris YOLOR) vers TFLite :

```bash
# Cloner le dépôt
git clone https://github.com/hunglc007/tensorflow-yolov4-tflite
cd tensorflow-yolov4-tflite

# Installer les dépendances
pip install -r requirements.txt

# Convertir le modèle
python save_model.py --weights ../models/yolor-p6.pt --output ./checkpoints/yolor-p6-640 --input_size 640 --model yolov4
python convert_tflite.py --weights ./checkpoints/yolor-p6-640 --output ./checkpoints/yolor_p6_640.tflite
```

## Utilisation du modèle converti

Une fois la conversion terminée, copiez le fichier `yolor_p6_640.tflite` dans le dossier `app/src/main/assets/` de votre projet Android :

```bash
cp yolor_p6_640.tflite chemin/vers/votre/projet/app/src/main/assets/
```

## Conversion directe YOLOv8 (Alternative plus simple)

Si vous rencontrez des difficultés avec la conversion de YOLOR, vous pouvez envisager d'utiliser YOLOv8 qui offre une conversion directe vers TFLite :

```bash
pip install ultralytics

# Télécharger et convertir YOLOv8s
from ultralytics import YOLO

# Télécharger le modèle
model = YOLO("yolov8s.pt")

# Exporter en TFLite
model.export(format="tflite", int8=True)
```

Dans ce cas, vous devrez modifier la variable `DEFAULT_MODEL_NAME` dans `ModelManager.kt` pour utiliser "yolov8s.tflite" à la place, et peut-être ajuster les dimensions d'entrée et le post-traitement.

## Ressources additionnelles

- [GitHub YOLOR](https://github.com/WongKinYiu/yolor)
- [TensorFlow Lite Model Conversion](https://www.tensorflow.org/lite/convert)
- [ONNX Runtime](https://onnxruntime.ai/)
- [Ultralytics YOLOv8](https://github.com/ultralytics/ultralytics) 