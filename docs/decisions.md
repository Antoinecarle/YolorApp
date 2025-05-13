# Journal des Décisions

Ce document sert à consigner les décisions techniques et produit importantes prises au cours du développement du projet, ainsi que leur justification.

## Décisions d'Architecture

| ID | Date | Décision | Justification | Alternatives considérées | Impact |
|----|------|----------|---------------|--------------------------|--------|
| A001 | - | Architecture MVVM avec Repository | L'architecture MVVM offre une séparation claire des préoccupations, facilitant les tests unitaires et la maintenance. Le pattern Repository permet d'isoler la logique d'accès aux données. | MVC (trop simple pour les besoins), MVP (moins adapté à Android moderne), Clean Architecture (trop complexe pour ce projet) | Influence la structure complète de l'application et le flux de données |
| A002 | - | Utilisation de fragments plutôt que d'activités multiples | Les fragments offrent une navigation plus fluide, partagent les ViewModels, et s'intègrent mieux avec Navigation Component. | Multiple activités (transitions moins fluides, partage d'état plus complexe) | Définit l'approche de navigation et de gestion d'état |
| A003 | - | Kotlin Coroutines pour l'asynchrone | Les coroutines sont plus légères que les threads, elles s'intègrent nativement avec Kotlin et offrent une gestion plus simple du cycle de vie | RxJava (courbe d'apprentissage plus élevée), Callbacks (code spaghetti potentiel) | Simplifie la gestion des opérations asynchrones comme l'inférence ML |

## Décisions Techniques

| ID | Date | Décision | Justification | Alternatives considérées | Impact |
|----|------|----------|---------------|--------------------------|--------|
| T001 | - | TensorFlow Lite comme moteur d'inférence principal | Support natif sur Android, outils d'optimisation bien développés, conversion facilité depuis ONNX | PyTorch Mobile (moins optimisé pour Android), ONNX Runtime (moins d'optimisations mobiles) | Détermine le format final du modèle et les performances |
| T002 | - | ONNX comme format intermédiaire de conversion | Format standard pour l'échange de modèles ML, facilite la conversion depuis PyTorch | Conversion directe PyTorch→TFLite (moins fiable et documentée) | Permet une flexibilité dans le pipeline de conversion |
| T003 | - | CameraX pour la capture d'image | API moderne avec gestion simplifiée du cycle de vie, prévisualisation et capture. Compatible avec les appareils récents. | Camera2 API (plus complexe et verbeuse), anciennes APIs Camera (dépréciées) | Simplifie l'interface caméra et assure la compatibilité |
| T004 | - | Quantification INT8 pour l'optimisation du modèle | Réduit significativement la taille du modèle (~75%) et améliore les performances d'inférence avec une perte de précision acceptable | Modèle FP32 (plus précis mais plus lent), FP16 (compromis modéré) | Influence directement les performances et la taille de l'app |
| T005 | - | DataStore pour les préférences utilisateur | API moderne remplaçant SharedPreferences, support des types, flows réactifs | SharedPreferences (API plus ancienne, moins typée) | Simplifie la gestion des préférences utilisateur |

## Décisions Produit

| ID | Date | Décision | Justification | Alternatives considérées | Impact |
|----|------|----------|---------------|--------------------------|--------|
| P001 | - | Focus sur les performances plutôt que la précision | Pour une utilisation mobile, des temps d'inférence courts sont prioritaires par rapport à la précision maximale | Précision maximale (temps d'inférence trop longs pour le mobile) | Définit l'équilibre entre performances et précision |
| P002 | - | Interface minimaliste centrée sur la caméra | Expérience utilisateur simplifiée, orientée vers la tâche principale (détection d'objets) | Interface complexe avec options avancées (confuserait les utilisateurs) | Détermine l'expérience utilisateur globale |
| P003 | - | Support pour les images de galerie en plus de la caméra | Offre plus de flexibilité aux utilisateurs pour tester le modèle sur différentes images | Seulement caméra en direct (moins flexible pour les tests) | Élargit les cas d'utilisation de l'application |
| P004 | - | Affichage des métriques de performance | Permet aux utilisateurs techniques d'évaluer les performances du modèle sur leur appareil | Interface simplifiée sans métriques (moins informative pour l'évaluation) | Ajoute de la valeur pour les utilisateurs techniques |

## Décisions de Sécurité et Conformité

| ID | Date | Décision | Justification | Alternatives considérées | Impact |
|----|------|----------|---------------|--------------------------|--------|
| S001 | - | Traitement des images en local uniquement | Aucune donnée utilisateur n'est envoyée à des serveurs externes, protégeant ainsi la vie privée | Traitement cloud (meilleures performances mais risques de confidentialité) | Assure la confidentialité des données utilisateur |
| S002 | - | Demande explicite des permissions | Conformité avec les bonnes pratiques Android pour les permissions de caméra et stockage | Demande groupée de permissions (moins respectueuse de la vie privée) | Améliore l'expérience utilisateur et la conformité |

## Notes et Observations

### Contraintes importantes
- Le modèle YOLOR est initialement volumineux et doit être significativement optimisé pour l'exécution mobile
- Le processus de conversion (PyTorch → ONNX → TFLite) peut introduire des différences de comportement
- L'équilibre entre taille du modèle, temps d'inférence et précision est critique

### Risques identifiés
- Certaines optimisations peuvent réduire la précision en dessous du seuil acceptable
- La conversion du modèle peut nécessiter des ajustements manuels de l'architecture
- Les performances sur des appareils moins puissants que le Pixel 8 pourraient être insuffisantes 