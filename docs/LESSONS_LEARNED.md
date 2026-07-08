# Défis et Apprentissages (Lessons Learned)

La construction d'une application aussi riche qu'Assabil a présenté plusieurs défis techniques majeurs. Ce document résume les problèmes rencontrés et les solutions apportées.

---

## Défi 1 : La gestion de l'Audio en Arrière-plan (Foreground Services)

**Problème :** 
Depuis Android 8 (Oreo) et encore plus depuis Android 12+, le système est extrêmement agressif envers les applications qui tournent en arrière-plan pour préserver la batterie. Lancer un son simple avec MediaPlayer se coupe dès que l'application passe en arrière-plan.

**Solution technique :**
Implémentation d'un `Foreground Service` (Service de premier plan). 
Il a fallu :
1. Déclarer le type de service `mediaPlayback` dans le Manifest.
2. Lier le service à une notification persistante contenant des contrôles (Play/Pause).
3. Gérer le Focus Audio (Audio Focus) pour que la lecture se mette en pause si l'utilisateur reçoit un appel téléphonique, et reprenne ensuite.

**Apprentissage :** La gestion du cycle de vie des médias sur Android nécessite une architecture robuste (MediaSessionCompat) pour bien s'interfacer avec l'écosystème du téléphone (écouteurs Bluetooth, commandes au volant, etc.).

---

## Défi 2 : Moteur de rendu de l'Arabe et Polices (Typography)

**Problème :** 
L'affichage correct des textes coraniques (Uthmani script) est complexe. Les diacritiques (Tashkeel) peuvent se superposer ou être coupés par les limites par défaut des `Text` composables. 

**Solution technique :**
* Recherche et intégration de polices TTF/OTF spécifiques au Coran optimisées pour l'affichage numérique.
* Modification des `LineHeight` et des marges dans Jetpack Compose pour assurer que les accents supérieurs et inférieurs ne soient jamais tronqués.
* Maintien du respect de la justification "Right-to-Left" (RTL) exigée par la langue arabe.

**Apprentissage :** Le support du RTL dans Compose est très performant nativement, mais requiert des tests visuels constants.

---

## Défi 3 : Boussole Qibla Stable

**Problème :** 
Les capteurs natifs (Accéléromètre et Magnétomètre) renvoient des valeurs très brutes et "bruitées". Si l'on met à jour l'UI directement avec ces valeurs, la boussole tremble violemment de manière illisible.

**Solution technique :**
Application d'un **Filtre Passe-Bas (Low-Pass Filter)**. Ce petit algorithme mathématique lisse les valeurs entrantes : 
`nouveauCap = (valeurCapteur * alpha) + (ancienCap * (1.0 - alpha))`
Cela donne une animation de rotation de la boussole d'une fluidité parfaite et agréable pour l'utilisateur.

**Apprentissage :** Comprendre l'intégration logicielle / matérielle (hardware) et le traitement des signaux basiques.

---

## Défi 4 : Migration complète vers Jetpack Compose

**Problème :** 
Passer d'une mentalité XML impérative à une mentalité Compose déclarative (State-Driven).

**Solution technique :**
Repenser entièrement la couche UI. Accepter que "l'UI est une fonction de l'état" (`UI = f(State)`). Structurer les ViewModels pour qu'ils émettent un seul flux de vérité (`UiState` scellé - Sealed Class) représentant Loading, Success, ou Error.

**Apprentissage :** Une fois la courbe d'apprentissage passée, le temps de développement de l'UI est divisé par deux et la quantité de bugs liés à l'état de la vue approche de zéro.
