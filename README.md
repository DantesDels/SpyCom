# SpyCom

Application de chat sécurisée en réseau local, développée en Java 17 avec interface graphique JavaFX, chiffrement AES-256-GCM et transfert de fichiers.

## Fonctionnalités

- **Chiffrement** : tous les messages sont chiffrés en AES-256-GCM
- **Salons** : salons publics et privés avec invitations, liste complète avec indicateur du salon actuel
- **Messages privés** : communication directe entre utilisateurs (`/msg`)
- **Transfert de fichiers** : envoi d'images, textes et fichiers quelconques avec boutons d'accès rapide au dossier de téléchargement
- **Markdown** : rendu des messages avec support markdown (titres, listes, code, gras, italique, barré)
- **Statuts** : online, busy, afk ou statut personnalisé
- **Autocomplétion** : suggestions des commandes avec Tab ou Entrée
- **Historique** : les 100 derniers messages par salon sont conservés côté serveur
- **Interface moderne** : thème sombre personnalisable via `Theme.java`, fenêtre en premier plan, pop-up de connexion intuitif
- **Multi-ligne** : support des messages sur plusieurs lignes avec Shift+Enter
- **Pseudos uniques** : le serveur refuse les doublons et renvoie vers la page de login

---

## Technologies requises

Voici tout ce qui doit être installé sur votre machine avant de pouvoir lancer l'application.

### 1. Java Development Kit (JDK) 17 ou supérieur

SpyCom est écrit en Java 17 et utilise JavaFX 17. Le JDK inclut le compilateur `javac` et l'environnement d'exécution `java`.

**Vérifier si Java est déjà installé :**
```bash
java -version
```
Vous devez voir une version 17 ou supérieure, par exemple :
```
openjdk version "17.0.9" 2023-10-17
```

**Installer le JDK 17 :**
- **Windows / Linux / Mac** : téléchargez-le depuis [Adoptium](https://adoptium.net/) (Eclipse Temurin 17) ou [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- **Windows (via winget)** :
  ```powershell
  winget install EclipseAdoptium.Temurin.17.JDK
  ```
- **Linux (Debian/Ubuntu)** :
  ```bash
  sudo apt update && sudo apt install openjdk-17-jdk
  ```
- **Mac (via Homebrew)** :
  ```bash
  brew install --cask temurin@17
  ```

Après installation, vérifiez que `JAVA_HOME` est bien défini :
```bash
# Windows PowerShell
echo $env:JAVA_HOME

# Linux / Mac
echo $JAVA_HOME
```

Si `JAVA_HOME` n'est pas défini, ajoutez-le manuellement :
- **Windows** : Paramètres > Système > Informations système > Paramètres avancés > Variables d'environnement > Nouvelle variable `JAVA_HOME` pointant vers le dossier du JDK (ex: `C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot`)
- **Linux/Mac** : ajoutez `export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")` dans votre `~/.bashrc` ou `~/.zshrc`

### 2. Gradle 9+

Gradle est l'outil de build qui compile le projet et gère les dépendances (notamment JavaFX). Il est utilisé pour lancer le serveur et les clients.

**Vérifier si Gradle est installé :**
```bash
gradle -v
```
Vous devez voir une version 9.x ou supérieure.

**Installer Gradle :**
- **Windows (via winget)** :
  ```powershell
  winget install Gradle.Gradle
  ```
- **Windows (manuel)** : téléchargez depuis [gradle.org/releases](https://gradle.org/releases/), extrayez et ajoutez le dossier `bin` au `PATH`
- **Linux (Debian/Ubuntu)** :
  ```bash
  sudo apt install gradle
  ```
- **Mac (via Homebrew)** :
  ```bash
  brew install gradle
  ```
- **Via SDKMAN** (toutes plateformes) :
  ```bash
  curl -s "https://get.sdkman.io" | bash
  sdk install gradle
  ```

### 3. Git

Git est nécessaire pour cloner le dépôt du projet.

**Vérifier si Git est installé :**
```bash
git --version
```

**Installer Git :**
- **Windows** : téléchargez depuis [git-scm.com](https://git-scm.com/download/win) ou via winget :
  ```powershell
  winget install Git.Git
  ```
- **Linux (Debian/Ubuntu)** :
  ```bash
  sudo apt install git
  ```
- **Mac** :
  ```bash
  xcode-select --install
  ```
  ou via Homebrew :
  ```bash
  brew install git
  ```

### 4. JavaFX

JavaFX est la bibliothèque graphique utilisée pour l'interface. Vous n'avez pas besoin de l'installer manuellement : il est automatiquement téléchargé par Gradle via le plugin `org.openjfx.javafxplugin` déclaré dans `build.gradle`. La version utilisée est **JavaFX 17.0.2**.

### Résumé des installations

| Outil | Version minimum | Commande de vérification |
|-------|----------------|------------------------|
| JDK | 17 | `java -version` |
| Gradle | 9+ | `gradle -v` |
| Git | 2.x | `git --version` |
| JavaFX | inclus par Gradle | — |

---

## Installation du projet

```bash
git clone https://github.com/DantesDels/SpyCom.git
cd SpyCom
```

Vérifiez que tout fonctionne en lançant un build :
```bash
gradle build -x test
```

Si le build réussit (`BUILD SUCCESSFUL`), vous êtes prêt.

---

## Lancer l'application en tant qu'hôte (serveur)

L'hôte est la machine qui héberge le serveur central auquel tous les clients se connectent. Une seule machine doit lancer le serveur.

### Étape 1 — Ouvrir un terminal

Ouvrez un terminal (PowerShell, CMD, Terminal, etc.) et placez-vous dans le dossier du projet :
```bash
cd chemin/vers/SpyCom
```

### Étape 2 — Lancer le serveur

```bash
gradle runServer
```

ou avec le script Windows :
```cmd
run server
```

Le serveur démarre sur le port **5000** par défaut. Vous devriez voir :
```
[Launcher] Demarrage du serveur sur le port 5000
[ Server ] En ecoute sur 192.168.1.XX:5000
```

**Notez l'adresse IP affichée** : c'est celle que les clients devront saisir pour se connecter.

### Étape 3 — Utiliser un port personnalisé (optionnel)

Pour lancer sur un autre port que 5000 :
```bash
gradle run -Pargs="server 6000"
```

### Étape 4 — Configurer le pare-feu (obligatoire pour les connexions externes)

Pour que les autres machines du réseau puissent atteindre votre serveur, le port TCP 5000 doit être ouvert dans le pare-feu de la machine hôte.

**Windows :**
```powershell
netsh advfirewall firewall add rule name="SpyCom" dir=in action=allow protocol=TCP localport=5000
```

**Linux (UFW) :**
```bash
sudo ufw allow 5000/tcp
```

**Mac (pf) :**
Ajoutez cette règle dans `/etc/pf.conf` :
```
pass in proto tcp from any to any port 5000
```
Puis rechargez :
```bash
sudo pfctl -f /etc/pf.conf
```

### Étape 5 — Trouver votre adresse IP locale

Communiquez cette IP aux autres utilisateurs.

**Windows :**
```powershell
ipconfig
```
Cherchez la ligne **"Adresse IPv4"** dans la section de votre connexion active (Wi-Fi ou Ethernet). Exemple : `192.168.1.100`.

**Linux / Mac :**
```bash
ip addr    # Linux
ifconfig   # Mac
```
Cherchez l'adresse `inet` correspondant à votre interface réseau active (ex: `wlan0` pour Wi-Fi, `eth0` pour Ethernet).

### Vérifier que le serveur est accessible

Sur la machine hôte elle-même, lancez un client pour tester :
```bash
gradle run -Pargs="localhost 5000 TestUser"
```

Si la connexion fonctionne en local, le serveur est prêt à recevoir d'autres machines.

---

## Se connecter en tant qu'utilisateur (client)

Les clients sont les utilisateurs qui se connectent au serveur lancé par l'hôte. Chaque utilisateur lance sa propre instance du client.

### Prérequis

- Avoir cloné le projet et installé les technologies listées ci-dessus (JDK, Gradle, Git)
- Être sur le même réseau local que la machine hôte (même Wi-Fi ou Ethernet)
- Connaître l'adresse IP de la machine hôte et le port du serveur

### Étape 1 — Ouvrir un terminal

```bash
cd chemin/vers/SpyCom
```

### Étape 2 — Lancer le client

**Méthode simple** (pop-up de connexion) :
```bash
gradle runClient
```

ou avec le script Windows :
```cmd
run client
```

Une fenêtre de connexion modale s'ouvre automatiquement avec trois champs :

| Champ | Description | Valeur par défaut |
|-------|-------------|-------------------|
| Adresse IP du serveur | L'IP locale de la machine hôte | `localhost` |
| Port | Le port du serveur | `5000` |
| Pseudo | Votre nom d'utilisateur | `Agent` + nombre aléatoire |

Remplissez l'IP communiquée par l'hôte, ajustez le port si nécessaire, choisissez un pseudo unique, puis cliquez **SE CONNECTER**.

**Méthode rapide** (paramètres en ligne de commande, bypass la pop-up partielle) :
```bash
gradle run -Pargs="192.168.1.100 5000 MonPseudo"
```

Les champs seront pré-remplis avec ces valeurs dans la pop-up de connexion.

### Étape 3 — Interface principale

Après connexion réussie, la fenêtre principale s'ouvre en premier plan avec :
- **Zone centrale** : historique des messages du salon actuel
- **Bas** : zone de saisie avec autocomplétion des commandes
- **Droite** : panneau latéral avec la liste des agents connectés et la liste des salons disponibles
- **Haut** : barre d'en-tête avec votre pseudo, votre statut et les infos de connexion

### Étape 4 — Si le pseudo est déjà pris

Si un autre utilisateur porte déjà le même pseudo, l'application vous renvoie automatiquement vers la page de login avec un message d'erreur en rouge indiquant que le pseudo est occupé. Choisissez-en un autre et reconnectez-vous.

### Étape 5 — Bouton IPCONFIG

Sur la page de connexion, le bouton **IPCONFIG** à droite du champ IP exécute la commande `ipconfig` et affiche le résultat dans une popup. Pratique pour retrouver rapidement l'IP du serveur si vous êtes sur la machine hôte.

### Mémorisation des paramètres

La dernière IP et le dernier port utilisés sont automatiquement sauvegardés dans `~/.spycom_prefs` et rechargés au prochain lancement. Vous n'avez pas à les ressaisir à chaque fois.

---

## Configuration réseau détaillée

### Sur le même réseau local (cas standard)

1. Toutes les machines doivent être connectées au même réseau (même box/routeur)
2. Les adresses IP commencent généralement par `192.168.x.x` ou `10.x.x.x`
3. Le port 5000 doit être ouvert dans le pare-feu de la machine hôte
4. Aucune configuration supplémentaire du routeur n'est nécessaire

### Connexion hors réseau local (avancé)

Pour que quelqu'un se connecte depuis un réseau différent (internet), deux étapes supplémentaires sont nécessaires :

1. **Redirection de port (NAT)** : accédez à l'interface de votre box internet (généralement `192.168.1.1` ou `192.168.0.1` dans un navigateur), trouvez la section "redirection de ports" ou "NAT/PAT", et créez une règle :
   - Port externe : `5000`
   - Port interne : `5000`
   - Protocole : TCP
   - IP destination : l'IP locale de la machine hôte

2. **IP publique** : communiquez votre IP publique aux utilisateurs distants. Elle est visible sur des sites comme [whatismyip.com](https://whatismyip.com) ou via :
   ```bash
   curl ifconfig.me
   ```

Attention : certaines box bloquent les connexions entrantes même avec une redirection de port configurée. Dans ce cas, utilisez un service de tunneling comme [ngrok](https://ngrok.com/).

---

## Dépannage de connexion

### Le client affiche "Erreur: Connection refused"

Le serveur n'est pas joignable. Causes possibles :
- Le serveur n'est pas lancé → lancez `gradle runServer` sur la machine hôte
- Mauvaise IP saisie → vérifiez l'IP avec `ipconfig` sur la machine hôte
- Pare-feu actif → ouvrez le port 5000 (voir section pare-feu ci-dessus)

### Le client tourne mais rien ne se passe

- Vérifiez que les deux machines sont sur le même réseau :
  ```powershell
  # Comparez les sous-réseaux (les 3 premiers chiffres de l'IP doivent correspondre)
  ipconfig
  ```

### Tester la connectivité directement

**Depuis la machine cliente, testez le port du serveur :**
```powershell
# Windows PowerShell
Test-NetConnection -ComputerAddress 192.168.1.100 -Port 5000
```
Si `TcpTestSucceeded` est `False`, c'est le pare-feu qui bloque ou le serveur n'est pas lancé.

```bash
# Linux / Mac
nc -zv 192.168.1.100 5000
```
Si vous voyez `Connection refused` ou `timed out`, même diagnostic.

### Désactiver temporairement le pare-feu (dernier recours)

**Windows :**
```powershell
# Désactiver
netsh advfirewall set allprofiles state off

# Réactiver après test
netsh advfirewall set allprofiles state on
```

**Linux (UFW) :**
```bash
sudo ufw disable
# Réactiver
sudo ufw enable
```

### Consulter les logs de debug

SpyCom génère un fichier de log détaillé dans votre répertoire utilisateur :
```
~/spycom_debug.log
```

Ce fichier trace :
- Initialisation de l'application et chargement des préférences
- Ouverture/fermeture du dialog de connexion
- Tentatives de connexion et erreurs
- Réception de messages (y compris `PSEUDO_TAKEN`)
- Déconnexions

Pour le consulter en temps réel :
```powershell
Get-Content ~\spycom_debug.log -Wait
```

---

## Interface utilisateur

### Connexion
- **Pop-up de connexion** : au lancement, une fenêtre modale demande l'IP du serveur, le port et le pseudo
- **Fenêtre en premier plan** : l'application s'ouvre automatiquement au-dessus des autres fenêtres
- **Pré-remplissage** : les champs sont pré-remplis avec les valeurs par défaut, les arguments passés en ligne de commande, ou les dernières valeurs utilisées (sauvegardées dans `~/.spycom_prefs`)
- **Bouton IPCONFIG** : exécute `ipconfig` et affiche le résultat pour retrouver facilement son IP

### Saisie de messages
- **Multi-ligne** : appuyez sur `Shift+Enter` pour insérer un retour à la ligne
- **Envoi** : `Enter` seul envoie le message
- **Autocomplétion** : les commandes sont suggérées automatiquement, validables avec `Tab` ou `Enter`
- **Navigation** : utilisez les flèches Haut/Bas pour parcourir les suggestions
- **Thème du menu** : le popup d'autocomplétion partage le thème sombre de l'interface

### Affichage des médias
Tous les fichiers partagés (images, textes, fichiers) affichent deux boutons :
- **Ouvrir** : ouvre le fichier avec l'application par défaut du système
- **Dossier** : ouvre l'explorateur de fichiers à l'emplacement du média dans `~/chat_downloads/`

Ces boutons sont visibles pour l'expéditeur comme pour les destinataires.

### Liste des salons
- **Indicateur visuel** : le salon actuel est marqué d'une flèche et affiché en vert gras
- **Tous les salons visibles** : y compris celui dans lequel vous vous trouvez
- **Clic sur un salon** : pré-remplit le champ de saisie avec `/join <salon>`

### Personnalisation du thème
Le thème est entièrement configurable dans `src/main/java/ui/Theme.java` :

| Constante | Couleur | Usage |
|-----------|---------|-------|
| `BG_DARK` | Fond principal | Message board, racine |
| `BG_PANEL` | Panneaux | Sidebar, header, input bar |
| `BG_INPUT` | Champs | TextFields, autocomplete, combos |
| `BG_TEXTAREA` | Zone de saisie | TextArea de message |
| `ACCENT` | Vert néon | Pseudos, titres, accent principal |
| `ACCENT_DIM` | Vert atténué | Bouton envoyer |
| `TEXT` | Gris clair | Texte des messages |
| `MUTED` | Gris foncé | Timestamps, placeholders |
| `AMBER` | Orange | Salons, label statut |
| `PRIVATE` | Rose | Messages privés |
| `BORDER` | Bleu-gris | Toutes les bordures |

---

## Commandes disponibles

Dans le client, tapez `/help` pour voir toutes les commandes :

| Commande | Description |
|----------|-------------|
| `/msg <pseudo> <texte>` | Envoyer un message privé à un utilisateur |
| `/join <salon>` | Rejoindre un salon public |
| `/join <salon> private` | Rejoindre un salon privé (invitation requise) |
| `/create <salon>` | Créer un nouveau salon public |
| `/create <salon> private` | Créer un nouveau salon privé |
| `/leave` | Quitter le salon actuel et revenir au salon général |
| `/invite <pseudo>` | Inviter un utilisateur dans votre salon privé |
| `/status <statut>` | Changer de statut (online/busy/afk) |
| `/rename <pseudo>` | Changer de pseudonyme |
| `/rooms` | Afficher la liste des salons disponibles |

---

## Architecture

```
SpyCom/
├── src/main/java/
│   ├── Main.java                 # Point d'entrée, choisit serveur ou client selon args
│   ├── launcher/
│   │   └── AppLauncher.java      # Dispatch vers ChatServer ou ChatApp
│   ├── server/
│   │   ├── ChatServer.java       # ServerSocket, boucle accept()
│   │   ├── ClientHandler.java    # Thread par client connecté
│   │   └── ClientRegistry.java   # Registre thread-safe (CopyOnWriteArrayList)
│   ├── client/
│   │   ├── ChatClient.java       # Socket client, méthodes send/joinRoom/disconnect
│   │   └── MessageReceiver.java  # Thread d'écoute des messages entrants
│   ├── protocol/
│   │   ├── Message.java          # Objet message (type, pseudo, contenu, timestamp, extra, encrypted)
│   │   ├── MessageType.java      # Enum des 18 types de messages
│   │   ├── MessageSerializer.java # Sérialisation pipe-delimited
│   │   └── Crypto.java           # Chiffrement/déchiffrement AES-256-GCM
│   └── ui/
│       ├── ChatApp.java          # Application JavaFX + dialog de connexion
│       ├── ChatController.java   # Contrôleur MVC (modèle ↔ vue)
│       ├── ChatView.java         # Assemblage du layout BorderPane
│       ├── InputBinder.java      # Gestion envoi, status, fichiers, drag-and-drop
│       ├── Theme.java            # Palette de couleurs et helpers de style
│       ├── cmd/
│       │   └── CommandHandler.java # Parsing et exécution des commandes /
│       ├── net/
│       │   ├── UserRoomManager.java # Parsing USER_LIST / ROOM_LIST
│       │   ├── FileReceiver.java    # Réassemblage chunks de fichiers
│       │   └── FileSaver.java       # Écriture dans ~/chat_downloads/
│       ├── panel/
│       │   ├── HeaderBar.java       # Barre du haut (titre, pseudo, statut)
│       │   ├── MessageBoard.java    # ListView des messages
│       │   ├── SidebarPane.java     # Liste des utilisateurs + salons
│       │   ├── InputPane.java       # Zone de saisie multi-ligne (TextArea)
│       │   ├── AutoCompletePopup.java # Popup stylisé d'autocomplétion
│       │   └── AutoCompleteLogic.java # Logique clavier (Tab/Enter/flèches)
│       └── render/
│           ├── MessageCell.java        # Rendu de cellule avec Markdown inline
│           ├── MessageRenderers.java   # Helpers pour image/texte/fichier/privé/système
│           ├── MessageTextRenderer.java # Rendu d'un message texte normal
│           ├── MarkdownRenderer.java    # Markdown bloc (titres, listes, code, citations)
│           ├── InlineParser.java        # Markdown inline (gras, italique, code, barré)
│           ├── TextFactory.java         # Création de nœuds Text stylisés
│           ├── FileDisplayNodes.java    # Composants UI pour les fichiers
│           └── UserCell.java            # Cellule utilisateur avec dot de statut coloré
└── build.gradle                  # Configuration Gradle + plugin JavaFX
```

---

## Protocole

Les messages transitent en TCP (port 5000 par défaut) au format texte délimité par pipes :

```
TYPE|pseudo|contenu|timestamp|extra|encrypted
```

Types de messages : `CONNECT`, `DISCONNECT`, `TEXT`, `SERVER_INFO`, `USER_LIST`, `JOIN_ROOM`, `LEAVE_ROOM`, `ROOM_LIST`, `PRIVATE_MSG`, `FILE_META`, `FILE_DATA`, `STATUS_CHANGE`, `NICK_CHANGE`, `INVITE_USER`, `PSEUDO_TAKEN`.

Les caractères spéciaux (`|`, `\`, `\n`) sont échappés dans la sérialisation.

---

## Sécurité

- **Chiffrement AES-256-GCM** : IV aléatoire généré pour chaque message, clé dérivée de `ChatApp-SharedSecret-2024` via SHA-256
- **Salons privés** : accès restreint par système d'invitations explicites
- **Pseudos uniques** : le serveur refuse les doublons à la connexion

⚠️ **Note** : La clé de chiffrement est codée en dur pour la démonstration. En production, utilisez un mécanisme d'échange de clés sécurisé (Diffie-Hellman, certificats TLS, etc.).

---

## Licence

Projet pédagogique — B2 Java Avancé.
