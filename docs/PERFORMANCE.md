# Performances et Optimisations

Pour garantir une expérience utilisateur fluide (cible de 60fps, voire 120fps sur les appareils compatibles) et limiter l'utilisation de la batterie, plusieurs stratégies d'optimisation ont été mises en œuvre dans Assabil.

---

## 1. Jetpack Compose : Best Practices

Jetpack Compose est puissant mais peut souffrir de re-compositions inutiles s'il est mal utilisé.
* **Stabilité des UI States** : Utilisation stricte de classes de données immuables (`data class`) pour les états de l'UI afin que Compose puisse "skipper" les recompositions si les données n'ont pas changé.
* **Lazy Lists (`LazyColumn` / `LazyRow`)** : Le chargement des listes de sourates et de versets se fait de manière "paresseuse" (lazy). Seuls les éléments visibles à l'écran sont rendus en mémoire, ce qui est crucial pour les longs textes comme le Coran.
* **`remember` et `derivedStateOf`** : Utilisation intensive de la mémorisation pour éviter les calculs lourds à chaque frame (ex: calculs d'animations pour la boussole Qibla).

---

## 2. Optimisation Base de Données (Room)

* **Pagination Locale** : Les données volumineuses ne sont pas chargées d'un seul coup.
* **Indexation** : Création d'index SQL sur des colonnes fréquemment recherchées (comme `surahId` dans la table `Ayah`) pour accélérer drastiquement les requêtes `SELECT`.
* **Flow** : La lecture asynchrone évite de bloquer le thread UI (ANR) même lors de requêtes massives.

---

## 3. Optimisation Réseau (Retrofit)

* **Politique de Cache Rigoureuse** : Les textes du Coran ne changent pas. L'application télécharge le texte en une fois et ne le requête plus jamais (sauf demande manuelle de l'utilisateur ou nettoyage du cache de l'app).
* **Compression GZIP** : OkHttp gère nativement la compression des requêtes, ce qui réduit considérablement la consommation de data de l'utilisateur.
* **Timeout & Retry** : Gestion intelligente des pertes de connexion (Timeouts) avec des messages explicites plutôt que des crashs silencieux.

---

## 4. Gestion de la Mémoire et de la Batterie

* **Fuites de Mémoire (Memory Leaks)** : 
  - L'utilisation de Kotlin Coroutines liées au `viewModelScope` assure que toutes les tâches asynchrones sont automatiquement annulées si l'utilisateur quitte l'écran. 
  - Finis les `AsyncTask` laissés en suspens !
* **Capteurs matériels (Boussole)** : 
  - L'abonnement au `SensorManager` est strict : il s'active dans le `onResume` du cycle de vie de l'écran et se désabonne immédiatement dans le `onPause`. Cela évite d'utiliser la batterie inutilement quand l'utilisateur ne regarde pas la Qibla.

---

## 5. Démarrage de l'Application (App Startup)

* Utilisation de la bibliothèque `androidx.startup` pour regrouper et différer l'initialisation de composants lourds. Cela permet d'afficher l'écran principal beaucoup plus rapidement au lancement de l'application.
