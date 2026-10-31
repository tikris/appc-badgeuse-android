# Publication sur Google Play

Compte développeur **personnel** : avant la mise en production, Google exige un **test fermé d'au moins 12 testeurs, inscrits sans interruption pendant 14 jours**. Comptez environ trois semaines entre le premier envoi et la publication.

```
Test interne (vous, tout de suite) → Test fermé (≥ 12 testeurs, 14 jours) → Demande d'accès à la production → Production
```

## 1. Clé d'envoi (une fois, sur le Mac)

```bash
keytool -genkeypair -v -keystore upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
base64 -i upload.jks | pbcopy        # → secret ANDROID_UPLOAD_KEYSTORE
```

Créez aussi les secrets suivants, dans *Settings → Secrets and variables → Actions* de ce dépôt :
- `ANDROID_KEYSTORE_PASSWORD` ;
- `ANDROID_KEY_ALIAS` (`upload`) ;
- `ANDROID_KEY_PASSWORD`.

Gardez une copie de `upload.jks` hors du dépôt. Lors du premier envoi, acceptez « Signature d'application Play » : Google garde alors la clé de signature finale, et une clé d'envoi perdue se remplace.

**Bundle signé** : *Actions → Android → Run workflow*, puis l'artefact `badge-creche-aab`.

## 2. Test interne (vos appareils)

*Tester → Test interne → Créer une version* : envoyez le `.aab`, puis ajoutez votre adresse Gmail comme testeur. Le lien d'inscription installe l'app depuis Play.

À vérifier :
- scan en direct, import d'une photo et d'un PDF ;
- QR code lu par la badgeuse ;
- widget et tuile des réglages rapides ;
- crèches proches.

## 3. Test fermé (au moins 12 testeurs, 14 jours)

1. Créez un **groupe Google** (groups.google.com), par exemple `badge-creche-testeurs`, ouvert à l'inscription.
2. *Tester → Test fermé → Gérer la piste → Testeurs* : ajoutez l'adresse du groupe, puis envoyez la même version.
3. Partagez dans la communauté WhatsApp le message ci-dessous. Visez **20 inscrits** pour absorber les désistements : il en faut 12 en continu.

> 📱 **Testeurs Android recherchés pour Badge Crèche !**
> L'app de l'APPC qui garde le QR code de la crèche toujours prêt arrive sur Android. Pour la publier, Google demande 12 testeurs pendant 14 jours.
> 1. Rejoignez le groupe : [lien du groupe Google]
> 2. Acceptez le test : [lien d'inscription Play]
> 3. Installez l'app, créez votre badge, essayez-le à la badgeuse et **gardez-la installée 14 jours**.
> Vos retours sont les bienvenus ici. Merci ! 🙏

4. Google regarde aussi si les testeurs **utilisent** l'app. Demandez des retours, et publiez une petite mise à jour pendant le test si des corrections sont nécessaires.

## 4. Fiche du Store (*Développer la présence sur le Store → Fiche principale*)

**Nom de l'application** (30 car. max)
```
Badge Crèche – APPC
```

**Description courte** (80 car. max)
```
Le QR code de votre crèche en un geste. Gratuit, sans compte ni publicité.
```

**Description complète**
```
Fini le badge de crèche oublié ou introuvable au fond du sac !

Badge Crèche garde le QR code remis par votre crèche en début d'année toujours prêt sur votre téléphone : un geste suffit pour le présenter à la badgeuse, à l'arrivée comme au départ.

EN TROIS ÉTAPES
• Choisissez votre crèche : les plus proches vous sont proposées, et toutes les crèches de Paris sont dans la liste.
• Scannez le QR code de la crèche, ou importez-le depuis une photo, une capture d'écran ou un PDF.
• Affichez-le d'un geste : widget sur l'écran d'accueil ou tuile des réglages rapides.

Le nom de l'enfant et celui de la crèche sont facultatifs : c'est vous qui choisissez ce qui s'affiche.

VOS DONNÉES VOUS APPARTIENNENT
Votre QR code, le nom de votre enfant et votre position ne quittent jamais votre téléphone. Aucun compte, aucune publicité, aucun traçage. Les données sont exclues des sauvegardes dans le cloud.

UNE INITIATIVE DE L'APPC
Badge Crèche est proposée gratuitement par l'association Parents à Paris Centre (APPC). Rejoignez la communauté des parents de Paris Centre depuis l'application !

Application indépendante, non affiliée à la Ville de Paris ni aux gestionnaires de crèches.
```

**Graphismes**
- Icône 512 × 512 : depuis `AppStore/icone-1024.png` du dépôt iOS, réduite.
- Bannière 1024 × 500 : à créer (fond APPC, icône, « Badge Crèche »).
- 4 captures de téléphone : prises pendant le test interne avec le QR code de démonstration ([`demo-qr.png`](https://tikris.github.io/appc-badgeuse/demo-qr.png)), jamais avec un vrai badge.

**Catégorie** : Parentalité. **Coordonnées** : `christophe.amalric@appc.paris`, site `https://tikris.github.io/appc-badgeuse/`.

## 5. Contenu de l'application (*Règles → Contenu de l'application*)

| Rubrique | Réponse |
|---|---|
| Règles de confidentialité | `https://tikris.github.io/appc-badgeuse/confidentialite.html` (section Android à ajouter) |
| Accès à l'application | Toutes les fonctionnalités sont accessibles sans accès spécial (pas de compte) |
| Annonces | Non |
| Classification du contenu | Questionnaire IARC, catégorie « Utilitaire » : « Non » partout → PEGI 3 |
| Public cible | 18 ans et plus, pas destinée aux enfants |
| Application d'actualités, santé, finance, gouvernement | Non (application indépendante) |
| Sécurité des données | Voir ci-dessous |

**Sécurité des données** : *« Votre application collecte-t-elle ou partage-t-elle des types de données utilisateur obligatoires ? » → **Non***.
- La position sert seulement sur le téléphone, à trier les crèches. Elle n'est ni transmise ni enregistrée : au sens de Google, elle n'est pas « collectée ».
- Le QR code est lu par ZXing, entièrement sur le téléphone. Aucun SDK n'envoie de données : ML Kit a été retiré, puisqu'il transmettait des diagnostics à Google.
- Les statistiques Matomo sont désactivées (`AppConfig.STATS_ENDPOINT` vide). Si elles sont branchées un jour, mettez à jour ce formulaire.

## 6. Demande d'accès à la production

Après les 14 jours, dans le *Tableau de bord → Demander l'accès à la production*, Google demande :
- comment les testeurs ont été recrutés : communauté WhatsApp de l'APPC ;
- comment ils ont utilisé l'app : badgeuses de plusieurs crèches ;
- quels retours ont été reçus et ce qui a changé.

Notez les retours au fil de l'eau pour pouvoir y répondre précisément. L'examen prend en général quelques jours.
