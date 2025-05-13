# Contexte du Projet YOLOR pour Android

## Objectif
Tester l'utilisation du modèle YOLOR (You Only Learn One Representation) pour la reconnaissance d'images sur une application Android, spécifiquement sur un appareil Pixel 8.

## Qu'est-ce que YOLOR?
YOLOR est un modèle de détection d'objets unifié développé par Chien-Yao Wang, I-Hau Yeh et Hong-Yuan Mark Liao, introduit dans l'article "You Only Learn One Representation: Unified Network for Multiple Tasks". Il s'agit d'une évolution des modèles YOLO qui propose une approche unifiée pour apprendre une représentation commune qui peut être utilisée pour différentes tâches.

## Défis à relever
- Conversion du modèle YOLOR (PyTorch) en un format compatible avec Android (TensorFlow Lite ou ONNX)
- Optimisation des performances pour l'exécution sur un appareil mobile
- Implémentation d'une interface caméra fonctionnelle pour capturer des images en temps réel
- Traitement des résultats de détection et affichage sur l'interface utilisateur

## Étapes clés
1. Mise en place du projet Android
2. Conversion du modèle YOLOR
3. Implémentation de l'interface caméra
4. Implémentation du pipeline d'inférence
5. Affichage des résultats
6. Tests de performance sur Pixel 8

## Références
- [Dépôt GitHub YOLOR original](https://github.com/WongKinYiu/yolor)
- [Article scientifique: "You Only Learn One Representation: Unified Network for Multiple Tasks"](https://arxiv.org/abs/2105.04206) 