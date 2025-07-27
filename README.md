# 🌍 MultiWorld Plugin - Spigot/Bukkit

**MultiWorld** est un plugin Minecraft pour gérer plusieurs mondes avec des spawns personnalisés, stockés dans une base de données via une API externe (`DatabaseAPI`). Il permet la création, la suppression, la téléportation et la gestion du spawn de mondes.

---

## 🔧 Fonctionnalités

- Commande `/spawn` pour téléporter un joueur au spawn global.
- Commande `/world` pour :
    - Créer un monde (`/world create <nom>`)
    - Supprimer un monde (`/world remove <nom>`)
    - Se téléporter dans un monde (`/world tp <nom> [joueur]`)
    - Définir le spawn d’un monde (`/world setspawn <nom>`)
- Téléportation automatique au spawn global lors de la première connexion.
- Sauvegarde et chargement des spawns via une base de données MySQL/SQLite via `DatabaseAPI`.

---

## 📁 Structure du plugin

### 📦 `fr.rudy.multiworld`

#### 🔹 `Main.java`
Classe principale (`JavaPlugin`) :
- Initialise la connexion via `DatabaseAPI`.
- Crée les tables SQL si elles n'existent pas.
- Instancie les managers : `CoreSpawnManager`, `WorldSpawnManager`.
- Enregistre les commandes et les listeners.

---

### 📂 `command`

#### 🔹 `SpawnTeleportCommand.java`
Commande `/spawn` :
- Vérifie que le joueur est bien un `Player`.
- Récupère le spawn global via `CoreSpawnManager`.
- Téléporte le joueur.

#### 🔹 `WorldCommand.java`
Commande `/world` :
- Vérifie les permissions (`newhorizon.admin.world`).
- Gère les sous-commandes : `create`, `remove`, `tp`, `setspawn`.
- Utilise `WorldSpawnManager` pour sauvegarder ou récupérer les spawns.

---

### 📂 `listener`

#### 🔹 `JoinSpawnListener.java`
Événement `PlayerJoinEvent` :
- Si le joueur ne s’est pas connecté depuis plus de 10 minutes,
- Téléporte automatiquement au spawn global une seconde après la connexion.

---

### 📂 `manager` *(ou `storage` si renommé)*

#### 🔹 `CoreSpawnManager.java`
Gère le **spawn global** :
- `setSpawn(Location)` : enregistre le spawn global en BDD.
- `getSpawn()` : récupère ce spawn depuis la BDD.

#### 🔹 `WorldSpawnManager.java`
Gère les **spawns spécifiques à chaque monde** :
- `setSpawn(String, Location)` : enregistre un spawn monde.
- `getSpawn(String)` : récupère ce spawn depuis la BDD.

---

## ✅ Prérequis

- Minecraft **Spigot/Paper 1.19+** (ajuste selon ta version exacte)
- Plugin **DatabaseAPI** installé

---

## 🚀 Installation

1. Compile le plugin avec **Maven/Gradle** ou exporte-le en `.jar`.
2. Place le fichier `.jar` dans le dossier `plugins/`.
3. Assure-toi que **DatabaseAPI** est présent dans les plugins.
4. Démarre le serveur Minecraft.

---

## 👨‍💻 Auteur

Développé par **Rudy**.
