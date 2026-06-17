# ChatApp

Application de chat sécurisée en Java avec interface graphique JavaFX, chiffrement AES-GCM et transfert de fichiers.

## Fonctionnalités

- **Chiffrement** : tous les messages sont chiffrés en AES-256-GCM
- **Salons** : salons publics et privés avec invitations
- **Messages privés** : communication directe entre utilisateurs
- **Transfert de fichiers** : envoi d'images, textes et fichiers quelconques
- **Markdown** : rendu des messages avec support markdown (titres, listes, code, gras, italique)
- **Statuts** : online, busy, afk ou statut personnalisé
- **Autocomplétion** : suggestions des commandes avec Tab
- **Historique** : les 100 derniers messages par salon sont conservés

## Prérequis

- Java 17 ou supérieur
- Gradle 9+

## Installation

```bash
git clone <url-du-repo>
cd chat-app
```

## Utilisation

### Lancer le serveur

```bash
gradle runServer
```

ou

```bash
run server
```

### Lancer un client

```bash
gradle runClient
```

ou

```bash
run
```

### Lancer avec des paramètres personnalisés

```bash
gradle run -Pargs="<host> <port> <pseudo>"
```

Exemple :

```bash
gradle run -Pargs="192.168.1.100 5000 Alice"
```

### Se connecter depuis une autre machine

Pour que d'autres utilisateurs sur le réseau local puissent rejoindre votre serveur :

1. **Lancez le serveur** sur la machine hôte :

   ```bash
   gradle runServer
   ```

2. **Trouvez l'adresse IP** de la machine hôte :
   - Windows : `ipconfig` (cherchez "Adresse IPv4", ex: `192.168.1.100`)
   - Linux/Mac : `ip addr` ou `ifconfig`

3. **Chaque utilisateur** lance un client en pointant vers cette IP :

   ```bash
   gradle run -Pargs="192.168.1.100 5000 MonPseudo"
   ```

   ou avec `run.bat` :

   ```bash
   run client
   ```

   puis modifiez les paramètres de connexion dans l'interface.

**Pare-feu** : assurez-vous que le port 5000 est ouvert sur la machine hôte. Sur Windows :

```powershell
netsh advfirewall firewall add rule name="ChatApp" dir=in action=allow protocol=TCP localport=5000
```

**Connexion distante (hors réseau local)** : si le serveur est derrière un routeur, redirigez le port 5000 vers la machine hôte dans l'interface de votre box, puis communiquez votre IP publique (visible sur https://whatismyip.com).

## Commandes disponibles

Dans le client, tapez `/help` pour voir toutes les commandes :

| Commande | Description |
|----------|-------------|
| `/msg <pseudo> <texte>` | Message privé |
| `/join <salon>` | Rejoindre un salon public |
| `/join <salon> private` | Rejoindre un salon privé |
| `/create <salon>` | Créer un salon public |
| `/create <salon> private` | Créer un salon privé |
| `/leave` | Retourner au salon général |
| `/invite <pseudo>` | Inviter un utilisateur dans votre salon privé |
| `/status <statut>` | Changer de statut (online/busy/afk) |
| `/rename <pseudo>` | Changer de pseudo |
| `/rooms` | Afficher la liste des salons |

## Architecture

```
chat-app/
├── src/main/java/
│   ├── Main.java              # Point d'entrée
│   ├── launcher/              # Gestion du lancement serveur/client
│   ├── server/                # Serveur de chat
│   │   ├── ChatServer.java    # Serveur principal
│   │   ├── ClientHandler.java # Gestion des connexions clients
│   │   └── ClientRegistry.java # Registre des clients et salons
│   ├── client/                # Client de chat
│   │   ├── ChatClient.java    # Client principal
│   │   └── MessageReceiver.java # Réception des messages
│   ├── protocol/              # Protocole de communication
│   │   ├── Message.java       # Modèle de message
│   │   ├── MessageType.java   # Types de messages
│   │   ├── MessageSerializer.java # Sérialisation
│   │   └── Crypto.java        # Chiffrement AES-GCM
│   └── ui/                    # Interface graphique JavaFX
│       ├── ChatApp.java       # Application JavaFX
│       ├── ChatController.java # Contrôleur MVC
│       ├── ChatView.java      # Vue principale
│       ├── InputBinder.java   # Gestion des entrées
│       ├── Theme.java         # Thème et styles
│       ├── cmd/               # Gestion des commandes
│       ├── net/               # Gestion réseau UI
│       ├── panel/             # Panneaux UI
│       └── render/            # Rendu des messages
└── build.gradle               # Configuration Gradle
```

## Protocole

Les messages sont transmis en texte brut sur TCP (port 5000 par défaut) au format :

```
TYPE|pseudo|contenu|timestamp|extra|encrypted
```

Les caractères spéciaux (`|`, `\`, `\n`) sont échappés.

## Sécurité

- **Chiffrement** : AES-256-GCM avec IV aléatoire pour chaque message
- **Clé partagée** : dérivée de `ChatApp-SharedSecret-2024` via SHA-256
- **Salons privés** : accès restreint par invitations

⚠️ **Note** : La clé est codée en dur pour la démonstration. En production, utilisez un système d'échange de clés (Diffie-Hellman, certificats, etc.).

## Licence

Projet de démonstration.
