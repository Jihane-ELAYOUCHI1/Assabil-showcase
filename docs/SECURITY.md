# Sécurité et Confidentialité

Assabil manipule des données d'usage privé et accède à certaines fonctionnalités du périphérique. La sécurité a été pensée *by design* dans l'architecture de l'application.

---

## 1. Philosophie "Privacy First"

* **Aucune Collecte Indésirable** : L'application fonctionne de manière autonome en grande partie. Elle ne requiert pas la création de compte utilisateur, garantissant un anonymat total.
* **Traitement Local** : La sauvegarde des favoris, de la progression de lecture et des statistiques du Tasbih se fait exclusivement en local dans la base de données SQLite de l'appareil (via Room).
* **Aucun Tracker Tierce** : L'application ne contient ni SDK publicitaires intrusifs, ni traqueurs comportementaux cachés.

---

## 2. Gestion des Permissions (Principe du moindre privilège)

L'application ne demande que ce qui est strictement nécessaire à son fonctionnement :

* **Localisation (`ACCESS_COARSE_LOCATION` & `ACCESS_FINE_LOCATION`)** : Demandée *uniquement* lors de l'accès à l'écran Qibla. Utilisée localement pour le calcul de l'azimut. Les données ne quittent jamais le périphérique.
* **Internet (`INTERNET`)** : Nécessaire uniquement pour le téléchargement initial du contenu (Sourates) et le streaming audio.
* **Notifications (`POST_NOTIFICATIONS`)** : Requise depuis Android 13 pour pouvoir afficher le contrôleur média du Foreground Service. Demandée explicitement à l'utilisateur.

---

## 3. Sécurité du Réseau

* **HTTPS par défaut** : Toutes les communications avec l'API (`api.alquran.cloud`) sont chiffrées via TLS/SSL.
* **Sécurité de la Configuration Réseau** : L'application respecte les politiques de sécurité strictes d'Android en bloquant par défaut tout trafic en clair (Cleartext Traffic).

---

## 4. Protection des Données et du Code

* **ProGuard / R8** : Lors de la génération des builds de production (`Release`), l'application utilise l'obfuscation et la minification (`isMinifyEnabled = true` habituellement en production). Cela complique grandement la rétro-ingénierie (Reverse Engineering) de l'APK.
* **Stockage Sécurisé** : L'accès direct à la base de données Room est bloqué par les mécanismes de "Sandbox" d'Android (une autre application ne peut pas lire ces fichiers).

---

*Note de sécurité sur ce dépôt Showcase : Conformément aux meilleures pratiques, aucun secret, fichier `google-services.json`, clé API privée ou configuration sensible du fichier gradle n'est publié ici.*
