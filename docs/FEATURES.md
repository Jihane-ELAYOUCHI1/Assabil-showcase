# Fonctionnalités d'Assabil

L'application Assabil intègre de nombreuses fonctionnalités réparties en plusieurs modules, afin de couvrir l'ensemble des besoins quotidiens de l'utilisateur.

---

## 📖 Module Coran (Al-Quran)

Le cœur de l'application est conçu pour offrir une expérience de lecture apaisante et personnalisable.

* **Lecture Intuitive** : Accès aux 114 sourates avec textes arabes originaux.
* **Traductions et Translittération** : Support de plusieurs langues (Français et Anglais) affichées en dessous de chaque verset (Ayah).
* **Moteur de Recherche** : Recherche instantanée d'une sourate spécifique.
* **Système de Favoris (Bookmarks)** : Sauvegarde des versets favoris ou des passages importants d'un simple clic pour les retrouver facilement.
* **Suivi de Progression** : L'application mémorise automatiquement le dernier verset lu, permettant à l'utilisateur de reprendre exactement là où il s'est arrêté.
* **Mise en cache hors-ligne** : Une fois téléchargées, les sourates sont disponibles en permanence sans connexion internet.

---

## 🎧 Module Audio (Récitation)

* **Lecteur Audio Intégré** : Écoute des récitations des sourates directement depuis l'application.
* **Lecture en arrière-plan** : Utilisation d'un `Foreground Service` pour continuer la lecture même lorsque l'écran est éteint ou que l'utilisateur navigue sur une autre application.
* **Contrôles depuis les notifications** : Play, pause, suivant, précédent accessibles directement depuis la barre de notification Android.

---

## 🕋 Module Qibla

* **Boussole Dynamique** : Une interface fluide indiquant précisément la direction de la Qibla.
* **Intégration Matérielle** : Exploite les capteurs natifs du téléphone (accéléromètre, magnétomètre, boussole) via un système réactif pour calculer l'azimut en temps réel.

---

## 📿 Module Adkars & Tasbih

* **Invocations Quotidiennes** : Adkars du matin, du soir, après la prière et avant le sommeil, catégorisés pour un accès rapide.
* **Support Multilingue** : Texte en arabe, phonétique et traduction.
* **Tasbih Électronique (Chapelet)** : 
  * Compteur interactif avec animation fluide.
  * Limitation automatique (ex: bloque à 33 ou 100).
  * Possibilité de réinitialiser le compteur de chaque session.
* **Favoris Adkar** : Possibilité d'épingler ses invocations préférées pour un accès prioritaire.

---

## 📜 Module Hadiths

* **Les 40 Hadiths de Nawawi** : Collection intégrée, lisible sans connexion.
* **Hadith du Jour** : Système aléatoire tirant un hadith différent chaque jour.
* **Favoris** : Possibilité de marquer un hadith pour y revenir plus tard.

---

## 🎨 Interface et Expérience (UI/UX)

* **Mode Sombre / Mode Clair** : Support natif en fonction des paramètres du système, utilisant des palettes de couleurs harmonieuses respectueuses de la vision nocturne.
* **Design "Edge-to-Edge"** : Le contenu s'étend sous la barre de statut et de navigation pour une immersion totale.
* **Navigation par Onglets (Bottom Bar)** : Accès immédiat aux 5 écrans principaux.

---

## ⚙️ Module Paramètres et Options

* Gestion des préférences utilisateurs (stockées localement).
* Contrôle de la langue et de l'affichage des traductions.
* Réinitialisation des favoris et de la progression.
