# Icônes SpyCom

Ce dossier contient les icônes de l'application.

## Formats supportés

L'application essaie de charger les icônes dans l'ordre suivant :

1. **icon.svg** (SVG) - Format vectoriel, support limité dans JavaFX
2. **icon.png** (PNG) - Format recommandé, support complet
3. **icon.ico** (ICO) - Format Windows, support complet

### Limitations du format SVG

JavaFX a un support limité pour les fichiers SVG :
- Les icônes de fenêtre (title bar) ne supportent pas nativement SVG
- L'affichage dans la boîte de dialogue de login peut avoir des limitations
- Pour de meilleurs résultats, utilisez PNG (64x64 ou 128x128 pixels)

Si vous avez un fichier SVG, nous recommandons de le convertir en PNG pour une compatibilité optimale.

## Fichiers requis

### icon.png (recommandé)
- **Usage** : Icône de la fenêtre JavaFX et de la boîte de dialogue de login
- **Format** : PNG avec transparence
- **Taille recommandée** : 64x64 ou 128x128 pixels
- **Placement** : Ce fichier doit être nommé exactement `icon.png`

### icon.ico
- **Usage** : Icône de l'exécutable Windows
- **Format** : ICO (Windows Icon)
- **Taille recommandée** : 256x256 pixels (avec plusieurs tailles intégrées)
- **Placement** : Ce fichier doit être nommé exactement `icon.ico`

### icon.svg (optionnel)
- **Usage** : Format vectoriel alternatif
- **Format** : SVG (Scalable Vector Graphics)
- **Taille** : Vectoriel (redimensionnable sans perte)
- **Placement** : Ce fichier doit être nommé exactement `icon.svg`
- **Note** : Support limité dans JavaFX, conversion en PNG recommandée

## Comment créer les icônes

### Option 1 : Utiliser un convertisseur en ligne
1. Créez ou trouvez une image PNG 256x256
2. Convertissez en ICO sur :
   - https://convertico.com/
   - https://icoconvert.com/
3. Placez les fichiers dans ce dossier

### Option 2 : Utiliser GIMP
1. Ouvrez votre image dans GIMP
2. Pour PNG : Fichier → Exporter → icon.png
3. Pour ICO : Fichier → Exporter → icon.ico (choisir "ICO" comme format)

### Option 3 : Utiliser ImageMagick (ligne de commande)
```bash
# Convertir PNG en ICO avec plusieurs tailles
convert icon.png -define icon:auto-resize=256,128,64,48,32,16 icon.ico

# Convertir SVG en PNG
convert -background none -density 300 icon.svg -resize 128x128 icon.png
```

### Option 4 : Convertir SVG en PNG
Si vous avez un fichier SVG, convertissez-le en PNG pour une meilleure compatibilité :

**Avec ImageMagick :**
```bash
convert -background none -density 300 icon.svg -resize 128x128 icon.png
```

**Avec Inkscape (ligne de commande) :**
```bash
inkscape icon.svg --export-type=png --export-width=128 --export-height=128 --export-filename=icon.png
```

**Avec un convertisseur en ligne :**
- https://cloudconvert.com/svg-to-png
- https://svgtopng.com/

## Vérification

Après avoir placé les icônes :

1. **Pour JavaFX** :
   ```bash
   gradle runClient
   ```
   L'icône devrait apparaître dans la barre de titre et dans la boîte de dialogue de login.

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
