# appc-badgeuse-android — Badge Crèche (Android)

Version Android de l'application **Badge Crèche** de l'association **Parents à Paris Centre (APPC)**, équivalente à la version iOS présente dans [appc-badgeuse](https://github.com/tikris/appc-badgeuse).

**Badge Crèche** permet aux parents d'avoir le QR code de la crèche toujours prêt et accessible en un geste :
- **Notification de proximité automatique sur l'écran verrouillé** dès l'arrivée devant la crèche (10 m, 25 m, 50 m ou désactivée).
- **Tuile dans les Paramètres Rapides (Quick Settings)** accessible d'un glissement depuis le haut de l'écran, y compris écran verrouillé.
- **Widget sur l'écran d'accueil** (conçu avec Jetpack Glance) affichant directement le QR code et les informations choisies.
- **Scanner intégré** avec CameraX & ML Kit : scan en direct de la caméra, sélection depuis la galerie/photos ou importation de fichier (PDF, image).
- **Annuaire des 1 176 crèches parisiennes** embarqué (`app/src/main/assets/creches-paris.json`, sous licence ODbL) : recherche instantanée tolérante aux fautes/accents et tri automatique par proximité GPS.
- **100 % local et respectueux de la vie privée** : le QR code, le nom de l'enfant et la position ne quittent jamais le smartphone. Les données sont stockées localement et exclues des sauvegardes cloud.

---

## Architecture technique

L'application est développée selon les meilleures pratiques Android modernes :

- **UI déclarative** : Jetpack Compose & Material 3 avec support Edge-to-Edge.
- **Scan de QR code** : CameraX + Google Play Services ML Kit Barcode Scanning + décodage haute résolution de PDF avec `PdfRenderer`.
- **Rendu du QR code** : Génération matricielle hors-ligne avec ZXing Core (haute netteté et contraste optimal pour les badgeuses).
- **Localisation & Geofencing** : Google Play Services Location (`FusedLocationProviderClient` et `GeofencingClient`) pour déclencher la notification à l'arrivée devant la crèche sans impacter la batterie.
- **Widget écran d'accueil** : Jetpack Glance AppWidget (Material 3).
- **Tuile système** : `TileService` dans la barre de réglages rapides Android.
- **Stockage local** : Jetpack DataStore Preferences avec sérialisation Kotlinx JSON.

---

## Prérequis et compilation

- Android Studio Koala / Ladybug ou supérieur
- JDK 17
- Android SDK 35 (Android 15)
- `minSdk` : 26 (Android 8.0 Oreo), compatible avec plus de 95 % du parc d'appareils actifs

### Lancer le projet

1. Ouvrir le dossier `appc-badgeuse-android` dans Android Studio.
2. Synchroniser le projet avec Gradle.
3. Lancer sur un appareil ou un émulateur (**Ctrl+R** ou **⌘R**).

---

## Licence

GPL v3 — Initiative de l'association **Parents à Paris Centre (APPC)**.
