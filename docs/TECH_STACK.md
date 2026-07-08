# Stack Technique

Cette page détaille les technologies clés utilisées dans le développement d'Assabil et les raisons motivant leur choix.

---

## 🚀 Langage et UI

### 1. Kotlin (Version 1.9+)
* **Pourquoi ?** Langage officiel recommandé par Google pour Android. Il offre une sécurité de type accrue (null-safety), une syntaxe concise et le support natif de la programmation asynchrone (Coroutines).

### 2. Jetpack Compose
* **Pourquoi ?** C'est le toolkit moderne de Google pour créer des interfaces natives de manière déclarative.
* **Avantages dans ce projet :**
  - Fin du couplage fastidieux XML/Kotlin.
  - Création rapide de composants réutilisables.
  - Gestion simplifiée des thèmes (Material 3) et du mode sombre natif.

### 3. Material Design 3 (M3)
* **Pourquoi ?** Permet d'offrir une expérience utilisateur fluide, familière et élégante (couleurs dynamiques, typographie, composants standardisés comme le `Scaffold`, les `Cards` et la `BottomAppBar`).

---

## 🏗 Architecture et Core

### 4. Architecture MVVM (Model-View-ViewModel)
* **Pourquoi ?** Sépare complètement l'interface utilisateur de la logique métier. Facilite les tests unitaires et garantit la préservation de l'état lors de la rotation de l'écran.

### 5. Hilt (Dagger)
* **Pourquoi ?** Injection de dépendances standard pour Android.
* **Avantages dans ce projet :**
  - Réduit considérablement le code boilerplate (`AppModule.kt`).
  - Gère automatiquement le cycle de vie des objets (`@Singleton`, `@ViewModelInject`).
  - Simplifie l'injection dans les ViewModels, Repositories et Services.

---

## 🗄 Persistance et Données

### 6. Room (SQLite)
* **Pourquoi ?** ORM officiel d'Android offrant une couche d'abstraction par-dessus SQLite.
* **Avantages dans ce projet :**
  - Permet le mode **hors-ligne** en mettant en cache les sourates, les adkars et les favoris.
  - Retourne des flux de données réactifs (`Flow`) via les DAOs.
  - Validation des requêtes SQL à la compilation.

### 7. Retrofit 2 & OkHttp 3
* **Pourquoi ?** Le client HTTP de référence pour Android.
* **Avantages dans ce projet :**
  - Gestion efficace des requêtes vers l'API `alquran.cloud`.
  - Conversion automatique des réponses JSON en objets Kotlin via Gson/Moshi.
  - Intercepteurs (`HttpLoggingInterceptor`) pour un débogage facile du réseau.

---

## ⚡ Asynchronisme et Multithreading

### 8. Coroutines & Flow (StateFlow)
* **Pourquoi ?** Remplace RxJava et les Callbacks traditionnels.
* **Avantages dans ce projet :**
  - Exécution de tâches lourdes (réseau, base de données) sans bloquer le Main Thread.
  - `StateFlow` est parfait avec Jetpack Compose pour réagir instantanément aux modifications d'état (ex: compteur du Tasbih, progression du lecteur).

---

## ⚙️ Services et Arrière-plan

### 9. Foreground Service (MediaPlayback)
* **Pourquoi ?** Requis par Android pour jouer l'audio du Coran même lorsque l'application est en arrière-plan. Assure l'affichage d'une notification persistante pour contrôler la lecture.

### 10. WorkManager
* **Pourquoi ?** Permet d'exécuter des tâches garanties en arrière-plan, même après redémarrage (`BootReceiver`).
* **Avantages dans ce projet :**
  - Utilisé pour mettre à jour le "Hadith du jour" de manière silencieuse et planifiée.

---

## 🗺 Navigation

### 11. Jetpack Navigation Compose
* **Pourquoi ?** Remplace le Navigation Component XML.
* **Avantages dans ce projet :**
  - Gestion des routes via des chaînes de caractères typées.
  - Gestion fluide des arguments (ex: `surahId` passé à `SurahDetailScreen`).
  - Intégration parfaite avec la BottomBar.
