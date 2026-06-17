# Guide de construction - SpyCom

## Générer un exécutable Windows (.exe)

### Prérequis
- JDK 17 ou supérieur installé
- Variable d'environnement `JAVA_HOME` configurée

### Étapes

1. **Compiler le projet**
   ```bash
   gradle jar
   ```

2. **Générer l'exécutable**
   ```bash
   gradle createExe
   ```

3. **Récupérer l'installateur**
   - L'exécutable se trouve dans `build/dist/SpyCom-1.0.0.exe`
   - Un dossier `SpyCom` est également créé avec l'application portable

### Options de l'installateur
L'installateur inclut :
- Choix du répertoire d'installation
- Création de raccourcis sur le bureau
- Ajout au menu Démarrer

## Icônes et tailles (Theme.java)

Les tailles sont centralisées dans `src/main/java/ui/Theme.java` :

| Constante           | Valeur | Usage |
|---------------------|--------|-------|
| `ICON_LOGIN_SIZE`   | 80     | `mainIcon.*` — page de connexion |
| `ICON_FAVICON_SIZE` | 32     | `favicon.*` — barre de titre / tâche |
| `ICON_EXE_SIZE`     | 256    | `icon.*` — exécutable Windows .exe |

## Formats par usage

| Usage                  | Fichier cible      | Format prioritaire | Raison                              |
|------------------------|--------------------|--------------------|--------------------------------------|
| Login dialog (JavaFX)  | `mainIcon.*`       | PNG > SVG > ICO    | JavaFX `ImageView` supporte PNG     |
| Window icon (JavaFX)   | `favicon.*`        | PNG > SVG > ICO    | JavaFX `Stage.getIcons()` préfère PNG |
| .exe icon (jpackage)   | `icon.*`           | ICO uniquement      | `jpackage --icon` sur Windows nécessite .ico |

Placez les fichiers dans `src/main/resources/` avec les noms exacts ci-dessus.

## Structure des ressources

```
src/main/resources/
├── mainIcon.png / .svg / .ico   # Icône du login (80×80)
├── favicon.png / .svg / .ico    # Icône de fenêtre (32×32)
└── icon.png / .svg / .ico       # Icône de l'exécutable (256×256)
```

## Générer l'exécutable

```bash
gradle jar
gradle createExe
```

L'icône `icon.ico` est intégrée automatiquement par jpackage.

## Dépannage

### L'icône ne s'affiche pas dans JavaFX
- Vérifier que le fichier est bien dans `src/main/resources/`
- Vérifier le nom exact : `icon.png` (minuscules)
- Consulter les logs : `~/spycom_debug.log`

### jpackage n'est pas trouvé
- Vérifier que `JAVA_HOME` est configuré
- Vérifier que `%JAVA_HOME%\bin\jpackage.exe` existe
- JDK 14+ requis (jpackage n'existe pas dans JDK 11)

### Erreur "JAR file not found"
- Exécuter d'abord : `gradle jar`
- Vérifier que `build/libs/chat-app.jar` existe

## Distribution

### Créer un package complet
```bash
# Compiler
gradle clean jar

# Générer l'exécutable
gradle createExe

# Le dossier build/dist contient :
# - SpyCom-1.0.0.exe (installateur)
# - SpyCom/ (application portable)
```

### Distribuer l'application
1. **Installateur** : `SpyCom-1.0.0.exe`
   - L'utilisateur lance l'installateur
   - Choix du répertoire d'installation
   - Raccourcis créés automatiquement

2. **Portable** : dossier `SpyCom/`
   - Copier le dossier complet
   - Lancer `SpyCom.exe` directement
   - Aucune installation requise

## Notes techniques

- **jpackage** est inclus dans JDK 14+
- L'exécutable inclut un JRE embarqué (pas besoin d'installer Java)
- Taille typique : ~80-100 MB (avec JRE)
- Compatible Windows 10/11
