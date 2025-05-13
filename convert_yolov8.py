from ultralytics import YOLO
import os
import shutil

def main():
    print("Téléchargement et conversion du modèle YOLOv8...")
    
    # Télécharger et charger le modèle YOLOv8n (vous pouvez utiliser s, m, l ou x pour des modèles plus grands)
    print("Téléchargement du modèle YOLOv8n...")
    model = YOLO("yolov8n.pt")  # Télécharge automatiquement depuis les serveurs Ultralytics
    
    # Exporter en format TensorFlow Lite
    print("Conversion du modèle en TensorFlow Lite...")
    success = model.export(format="tflite", int8=False)  # Utiliser int8=True pour la quantification
    
    if success:
        tflite_path = "yolov8n.tflite"
        target_dir = "app/src/main/assets"
        
        # Vérifier si le répertoire existe
        if not os.path.exists(target_dir):
            print(f"Création du répertoire {target_dir}...")
            os.makedirs(target_dir, exist_ok=True)
        
        # Copier le modèle dans le dossier assets
        target_path = os.path.join(target_dir, "yolov8n.tflite")
        print(f"Copie du modèle vers {target_path}...")
        shutil.copy2(tflite_path, target_path)
        
        print(f"Conversion terminée. Le modèle a été copié dans: {target_path}")
        print("YOLOv8n est prêt à être utilisé dans l'application Android")
    else:
        print("Erreur lors de la conversion du modèle.")

if __name__ == "__main__":
    main() 