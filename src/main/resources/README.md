# Icônes SpyCom

Ce dossier contient les icônes de l'application.

## Tailles définies dans Theme.java

Les tailles des icônes sont centralisées dans `Theme.java` :

| Constante           | Valeur | Usage |
|---------------------|--------|-------|
| `ICON_LOGIN_SIZE`   | 80     | `mainIcon.*` — page login |
| `ICON_FAVICON_SIZE` | 32     | `favicon.*` — barre de titre |
| `ICON_EXE_SIZE`     | 256    | `icon.*` — exécutable .exe |

Modifiez ces constantes dans `Theme.java` pour redimensionner les icônes.

## Formats par usage

| Usage                  | Priorité de chargement | Raison                              |
|------------------------|------------------------|--------------------------------------|
| Login dialog (JavaFX)  | PNG → SVG → ICO        | JavaFX `ImageView` supporte PNG     |
| Window icon (JavaFX)   | PNG → SVG → ICO        | JavaFX `Stage.getIcons()` préfère PNG |
| .exe icon (jpackage)   | ICO uniquement         | `jpackage --icon` sur Windows nécessite .ico |

## Fichiers disponibles

Chaque icône existe en 3 formats :

| Fichier          | Usage principal                      |
|------------------|--------------------------------------|
| `mainIcon.*`     | Affichée dans le dialogue de connexion (80×80) |
| `favicon.*`      | Icône de la fenêtre (barre de titre, 32×32) |
| `icon.*`         | Icône de l'exécutable Windows (.exe, 256×256) |

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
   L'icône `favicon.svg` (32x32) apparaît dans la barre de titre, `mainIcon.svg` (80x80) dans le login.

2. **Pour l'exécutable** :
   ```bash
   gradle jar
   gradle createExe
   ```
   L'icône `icon.svg` (256x256) est utilisée pour le fichier .exe.

## Icône par défaut

Si aucune icône n'est présente :
- JavaFX utilisera l'icône par défaut du système
- L'exécutable utilisera l'icône Java par défaut
- L'application fonctionnera normalement
