# Icônes SpyCom

Ce dossier contient les icônes de l'application.

## Fichiers requis

### icon.png
- **Usage** : Icône de la fenêtre JavaFX
- **Format** : PNG avec transparence
- **Taille recommandée** : 64x64 pixels
- **Placement** : Ce fichier doit être nommé exactement `icon.png`

### icon.ico
- **Usage** : Icône de l'exécutable Windows
- **Format** : ICO (Windows Icon)
- **Taille recommandée** : 256x256 pixels (avec plusieurs tailles intégrées)
- **Placement** : Ce fichier doit être nommé exactement `icon.ico`

## Comment créer les icônes

### Option 1 : Utiliser un convertisseur en ligne
1. Créez ou trouvez une image PNG 256x256
2. Convertissez en ICO sur :
   - https://convertico.com/
   - https://icoconvert.com/
3. Placez les deux fichiers dans ce dossier

### Option 2 : Utiliser GIMP
1. Ouvrez votre image dans GIMP
2. Pour PNG : Fichier → Exporter → icon.png
3. Pour ICO : Fichier → Exporter → icon.ico (choisir "ICO" comme format)

### Option 3 : Utiliser ImageMagick (ligne de commande)
```bash
# Convertir PNG en ICO avec plusieurs tailles
convert icon.png -define icon:auto-resize=256,128,64,48,32,16 icon.ico
```

## Vérification

Après avoir placé les icônes :

1. **Pour JavaFX** :
   ```bash
   gradle runClient
   ```
   L'icône devrait apparaître dans la barre de titre.

2. **Pour l'exécutable** :
   ```bash
   gradle jar
   gradle createExe
   ```
   L'icône devrait apparaître sur le fichier .exe généré.

## Icône par défaut

Si aucune icône n'est présente :
- JavaFX utilisera l'icône par défaut du système
- L'exécutable utilisera l'icône Java par défaut
- L'application fonctionnera normalement

## Ressources utiles

- [Icônes gratuites](https://www.flaticon.com/)
- [IconFinder](https://www.iconfinder.com/)
- [The Noun Project](https://thenounproject.com/)

Recherchez des icônes avec les mots-clés : "chat", "message", "secure", "communication"
