# Architecture d'Assabil

L'application Assabil est construite sur une architecture moderne, modulaire et évolutive, respectant scrupuleusement les principes de la **Clean Architecture** et du motif **MVVM** (Model-View-ViewModel). 

Cette approche garantit une forte séparation des responsabilités, une grande testabilité et une maintenance facilitée.

---

## Architecture Globale

Le projet est structuré en trois grandes couches distinctes :

1. **Couche UI (Présentation)**
2. **Couche Domain (Logique Métier)**
3. **Couche Data (Données)**

L'injection de dépendances est orchestrée par **Hilt / Dagger**, permettant de fournir les instances nécessaires (Base de données, API, Repositories) à l'ensemble des composants sans couplage fort.

---

## 1. Couche UI (Présentation)

La couche UI est entièrement développée avec **Jetpack Compose** et repose sur les principes de l'UI déclarative.

- **Composants (Screens & Navigation)** : Les écrans (`QuranScreen`, `KiblaScreen`, `HadithAdkarScreen`, etc.) sont des fonctions composables indépendantes. La navigation est gérée par `Jetpack Navigation Compose` (`AsSabilNavHost`).
- **ViewModels** : Chaque écran majeur est couplé à un ViewModel dédié (`ViewModels.kt`). Le ViewModel gère l'état de l'interface (`UI State`) et expose les données sous forme de `StateFlow`.
- **Unidirectional Data Flow (UDF)** : Les interactions de l'utilisateur remontent vers le ViewModel sous forme d'intentions/événements. Le ViewModel met à jour l'état, qui est redescendu automatiquement aux composants Compose pour le rendu.

---

## 2. Couche Domain (Logique Métier)

La couche domaine est le cœur de l'application, complètement isolée des bibliothèques Android et des sources de données externes.

- **Modèles (`domain/model/Models.kt`)** : Contient les objets métiers purs (ex: `Surah`, `Ayah`, `Adkar`, `Hadith`).
- **Absence de dépendances externes** : Cette couche ne connaît ni Room, ni Retrofit, ni Compose. Elle décrit *ce que* fait l'application, indépendamment de *comment* elle le fait.

---

## 3. Couche Data (Données)

La couche de données est responsable de la gestion, de la récupération et de la persistance des données. Elle est organisée selon le **Repository Pattern**.

### Repository Pattern
Les Repositories (`QuranRepository`, `AdkarRepository`, `HadithRepository`) servent de source unique de vérité (`Single Source of Truth`). 
Leur rôle est de décider s'il faut récupérer les données depuis le réseau ou la base de données locale.

### Sources de données

#### A. Source Locale (Base de données)
- **Room (`AsSabilDatabase.kt`)** : Base de données SQLite locale.
- **Entités (`entity/Entities.kt`)** : Représentations des tables (ex: `SurahEntity`, `AyahEntity`, `BookmarkEntity`).
- **DAO (`dao/Daos.kt`)** : Interfaces définissant les requêtes SQL via des annotations (`@Query`, `@Insert`). Les résultats sont renvoyés sous forme de `Flow` pour des mises à jour réactives.

#### B. Source Distante (Réseau)
- **Retrofit & OkHttp (`api/QuranApiService.kt`)** : Gestion des appels API vers *api.alquran.cloud*.
- **DTOs** : Les réponses JSON sont converties en objets de transfert de données, puis mappées en entités Room ou objets Domain par le Repository.

---

## Flux des Données

1. **Appel UI** : Un événement UI déclenche une fonction dans le ViewModel (ex: `loadSurahs()`).
2. **ViewModel -> Repository** : Le ViewModel appelle le Repository via les Coroutines.
3. **Repository -> Local/Remote** : 
   - Le Repository vérifie le cache local via le DAO.
   - Si les données manquent, il appelle l'API distante via Retrofit.
   - Les nouvelles données sont sauvegardées en local via Room.
4. **Retour Réactif** : Les DAOs exposent des `Flow`. Dès qu'une donnée est insérée, le Flow émet une nouvelle valeur.
5. **Mise à jour UI** : Le Repository transmet le Flow au ViewModel, qui met à jour le `StateFlow` écouté par Jetpack Compose. L'écran se redessine automatiquement.
