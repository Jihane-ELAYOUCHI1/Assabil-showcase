# UI / UX Design

L'interface et l'expérience utilisateur d'Assabil ont été conçues avec une attention particulière à la sobriété, au respect des codes de l'application religieuse, et aux normes modernes de **Material Design 3**.

---

## 🎨 Philosophie de Design

* **Sérénité et Minimalisme** : L'utilisation de l'application ne doit générer aucune fatigue visuelle ou surcharge cognitive. Les interfaces sont épurées, se concentrant sur l'essentiel : le texte sacré et les invocations.
* **Palette de Couleurs** : 
  - Utilisation de teintes douces, naturelles et organiques.
  - Ex: Un "Cream" élégant en fond pour réduire l'éblouissement comparé à un blanc pur.
  - Des touches de vert (symbole traditionnel) subtiles pour l'interactivité.
* **Typographie** : 
  - Textes arabes : Polices optimisées pour la lisibilité de la calligraphie (ex: Uthmani script) pour s'assurer que les diacritiques (Tashkeel) soient parfaitement distincts.
  - Textes latins : Sans-serif moderne (Roboto/Inter) pour la traduction, offrant un excellent contraste avec le texte arabe.

---

## 📱 Composants Jetpack Compose

L'ensemble de l'UI est modulaire :

* **AsSabilBottomBar** : Barre de navigation inférieure persistante avec icônes adaptatives (remplies quand actives, contour quand inactives).
* **Scaffold** : Permet la gestion automatique des espacements (PaddingValues) entre la barre du haut, le contenu central et la barre du bas.
* **Cartes (Cards)** : Utilisation intensive des `ElevatedCard` avec coins arrondis pour séparer chaque Ayah ou Hadith, rendant le défilement agréable.

---

## 🌗 Responsive & Adaptabilité

* **Mode Sombre (Dark Theme)** : L'application bascule automatiquement sur un thème sombre profond. Les couleurs principales sont inversées mathématiquement pour conserver l'accessibilité textuelle (WCAG) sans agresser l'œil de nuit (notamment lors de la prière du Fajr ou du Tahajjud).
* **Edge-to-Edge** : L'implémentation de la transparence dans les barres de système (Status bar / Navigation bar) donne un aspect "Premium" et infini à l'application.

---

## ♿ Accessibilité (A11y)

* **Tailles des cibles** : Les boutons et compteurs (Tasbih) ont une zone tactile minimum de 48x48dp.
* **Support du "TalkBack"** : Utilisation des modificateurs de sémantique (`Modifier.semantics`) dans Compose pour que les utilisateurs malvoyants puissent naviguer vocalement.
* **Contraste** : Vérification des contrastes des couleurs selon les normes de Material 3.

---

## ✨ Micro-Animations

* **Ripple Effects** : Retour visuel immédiat lors des interactions (clics sur les chapitres, tasbih).
* **Transitions de Navigation** : Jetpack Navigation Compose assure des fondus enchaînés ou des glissements lors du passage d'un écran à l'autre, évitant l'effet de saccade brutal des anciennes activités Android.
