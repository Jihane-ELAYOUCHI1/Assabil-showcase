# Diagrammes

Les diagrammes ci-dessous illustrent la conception de l'application Assabil à différents niveaux d'abstraction.

---

## 1. Architecture Globale (Clean Architecture & MVVM)

```mermaid
graph TD
    subgraph UI Layer
        A[Jetpack Compose Screens] -->|Events / Actions| B(ViewModels)
        B -->|UI State Flow| A
    end

    subgraph Domain Layer
        B -->|Suspend fun / Flow| C(Models / Use Cases)
    end

    subgraph Data Layer
        C -->|Domain Models| D{Repositories}
        D -->|Flow / Mappers| C
        
        D -->|Fetch Data| E[Retrofit API]
        D -->|CRUD Operations| F[(Room Database)]
        
        E -->|JSON Responses| D
        F -->|Entities Flow| D
    end
```

---

## 2. Navigation (Jetpack Navigation Compose)

```mermaid
graph LR
    Main[MainActivity] --> NavHost{AsSabilNavHost}
    NavHost --> BottomBar[AsSabilBottomBar]
    
    NavHost --> Home((Home Screen))
    NavHost --> Quran((Quran Screen))
    NavHost --> Qibla((Qibla Screen))
    NavHost --> Adkar((Hadith & Adkar Screen))
    NavHost --> Settings((Settings Screen))
    
    Quran --> SurahDetail((Surah Detail Screen))
```

---

## 3. Flux Utilisateur : Lecture du Coran

```mermaid
sequenceDiagram
    actor User
    participant UI as QuranScreen
    participant VM as QuranViewModel
    participant Repo as QuranRepository
    participant DB as Room (Local)
    participant API as Retrofit (Remote)

    User->>UI: Ouvre la page du Coran
    UI->>VM: getSurahs()
    VM->>Repo: getAllSurahs()
    Repo->>DB: Observe les entités (Flow)
    DB-->>Repo: Liste vide
    Repo->>API: fetchAndCacheSurahs()
    API-->>Repo: JSON Response
    Repo->>DB: insertAll(Surahs)
    DB-->>Repo: Flow émet la nouvelle liste
    Repo-->>VM: Liste Domain Models
    VM-->>UI: Met à jour le StateFlow
    UI-->>User: Affiche la liste des sourates
    
    User->>UI: Clique sur une Sourate
    UI->>UI: Navigue vers SurahDetailScreen
```

---

## 4. Flux API et Gestion du Cache hors-ligne

```mermaid
graph TD
    Start((Début de la requête)) --> CacheCheck{Données en cache local ?}
    
    CacheCheck -- Oui --> ReturnCache[Retourner les données depuis Room]
    CacheCheck -- Non --> FetchNetwork[Appel réseau via Retrofit]
    
    FetchNetwork --> Success{Succès ?}
    Success -- Oui --> SaveCache[(Sauvegarde dans Room)]
    SaveCache --> ReturnCache
    
    Success -- Non --> Error[Gérer l'erreur / Afficher un message]
    Error --> ReturnOldCache[Retourner les données périmées si existantes]
```

---

## 5. Base de données (UML Simplifié)

```mermaid
erDiagram
    SURAH ||--o{ AYAH : contient
    SURAH {
        int id PK
        string name
        string englishName
        string frenchName
        int versesCount
    }
    AYAH {
        int globalNumber PK
        int surahId FK
        string arabicText
        string englishTranslation
        string frenchTranslation
    }
    BOOKMARK {
        int id PK
        int surahId FK
        int ayahNumber
        string ayahText
        date savedAt
    }
    READING_PROGRESS {
        int id PK
        int lastSurahId
        int lastAyahNumber
        date lastReadAt
    }
    ADKAR {
        int id PK
        string sectionId
        string arabicText
        int currentCount
        boolean isFavorite
    }
```
