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

## Ajouter une icône personnalisée

### Pour l'application JavaFX (icône de fenêtre)

1. **Préparer l'icône**
   - Format : PNG (recommandé)
   - Taille : 32x32, 64x64, ou 128x128 pixels
   - Transparence : supportée

2. **Placer le fichier**
   - Nommer le fichier `icon.png`
   - Le placer dans `src/main/resources/`

3. **Vérifier**
   - L'icône sera chargée automatiquement au démarrage
   - Elle apparaîtra dans la barre de titre et la barre des tâches

### Pour l'exécutable Windows (icône du fichier .exe)

1. **Préparer l'icône**
   - Format : ICO (obligatoire pour Windows)
   - Taille : 256x256 pixels (contient plusieurs tailles)
   - Outils de conversion :
     - [ConvertICO](https://convertico.com/)
     - [ICO Convert](https://icoconvert.com/)
     - GIMP : exporter en .ico

2. **Placer le fichier**
   - Nommer le fichier `icon.ico`
   - Le placer dans `src/main/resources/`

3. **Générer l'exécutable**
   ```bash
   gradle createExe
   ```
   - L'icône sera automatiquement intégrée à l'exécutable

### Icônes recommandées

Pour un résultat professionnel, préparez :
- `icon.png` : 64x64 pixels (pour JavaFX)
- `icon.ico` : 256x256 pixels avec plusieurs tailles intégrées (pour Windows)

## Structure des ressources

```
src/main/resources/
├── icon.png    # Icône de l'application JavaFX
└── icon.ico    # Icône de l'exécutable Windows
```

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
