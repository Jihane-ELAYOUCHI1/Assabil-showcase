# Workflow Utilisateur

Ce document décrit le parcours type d'un utilisateur au sein de l'application Assabil, depuis son premier lancement jusqu'à la fermeture de l'application.

---

## 1. Démarrage (Cold Start)

1. **Lancement de l'application** : L'utilisateur appuie sur l'icône de l'application. Le thème `EdgeToEdge` se met en place instantanément (barre de statut transparente).
2. **Initialisation en Arrière-plan** : 
   - `Hilt` injecte les dépendances essentielles.
   - `WorkManager` via `BootReceiver` vérifie si les tâches en arrière-plan (ex: Hadith du jour) doivent être planifiées.
   - Si c'est la première ouverture, un pré-remplissage de la base de données Room peut se produire.
3. **Écran d'Accueil** : L'utilisateur arrive sur le `HomeScreen` (via le `AsSabilNavHost`). La navigation principale (`BottomBar`) est visible en bas de l'écran.

---

## 2. Navigation Quotidienne

### Scénario A : Continuer sa lecture du Coran
1. Depuis l'accueil, l'utilisateur voit un encart "Reprendre la lecture" (grâce à `ReadingProgressDao`).
2. Il clique sur le bouton. L'application navigue instantanément vers `SurahDetailScreen` en passant l'ID de la sourate et en faisant défiler l'écran jusqu'à l'Ayah précis où il s'était arrêté.
3. Pendant sa lecture, l'utilisateur souhaite sauvegarder une Ayah particulièrement touchante : il appuie sur l'icône de signet (Bookmark). Le ViewModel appelle le Repository qui l'insère dans Room en un clin d'œil.

### Scénario B : Écoute Audio
1. L'utilisateur lance la lecture audio depuis le détail d'une sourate.
2. Le `QuranAudioService` est instancié en tant que *Foreground Service*.
3. L'utilisateur verrouille son téléphone et le met dans sa poche. La récitation continue. Une notification lui permet de mettre en pause depuis l'écran de verrouillage.

### Scénario C : Vérifier la Qibla
1. L'utilisateur se trouve dans un nouveau lieu et appuie sur l'onglet **Qibla**.
2. Une demande de permission de localisation (coarse/fine location) s'affiche s'il ne l'a pas déjà acceptée.
3. Le ViewModel de la Qibla s'abonne aux capteurs matériels (Compass/Magnetometer).
4. La boussole virtuelle à l'écran tourne de manière fluide, pointant vers la Kaaba avec un indicateur visuel lorsqu'il est parfaitement aligné.

### Scénario D : Session de Tasbih et Adkars
1. Après la prière, l'utilisateur ouvre l'onglet **Adkar**.
2. Il choisit "Adkar après la prière".
3. L'application charge instantanément les données depuis Room. L'utilisateur lit chaque Adkar.
4. Un bouton compteur (ex: 0/33) permet de faire le Tasbih. À chaque tap, un effet visuel (Ripple) et parfois haptique se déclenche, et l'entité locale est mise à jour. Une fois le quota atteint, le composant change de couleur.

---

## 3. Options et Personnalisation

1. L'utilisateur va dans l'onglet **Paramètres**.
2. Il décide de désactiver la traduction française pour ne garder que l'arabe et l'anglais.
3. Cette préférence est sauvegardée via `DataStore` ou `SharedPreferences`. Les `StateFlow` des ViewModels répercutent ce changement globalement, et les interfaces de lecture se mettent à jour automatiquement sans avoir à redémarrer l'application.

---

## 4. Fermeture et Arrière-plan

1. L'utilisateur quitte l'application (bouton Home ou retour arrière).
2. L'interface est détruite, mais les *Foreground Services* (si l'audio tourne) ou les *WorkManagers* continuent de vivre de manière optimisée pour économiser la batterie, conformément aux directives Android modernes.
