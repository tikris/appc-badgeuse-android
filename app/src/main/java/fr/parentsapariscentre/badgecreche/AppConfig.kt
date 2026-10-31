package fr.parentsapariscentre.badgecreche

/**
 * Textes, liens et configuration propres à l'APPC.
 * Identique à la version iOS pour une parfaite cohérence.
 */
object AppConfig {
    const val APP_NAME = "Badge Crèche"

    // MARK: Association
    const val ASSOCIATION_NAME = "Parents à Paris Centre"
    const val ASSOCIATION_SHORT_NAME = "APPC"
    const val WHATSAPP_COMMUNITY_URL = "https://chat.whatsapp.com/CiqGioqeO4yA93wTwvFJMW"

    // MARK: Liens
    const val PRIVACY_POLICY_URL = "https://tikris.github.io/appc-badgeuse/confidentialite.html"
    const val LEGAL_NOTICE_URL = "https://tikris.github.io/appc-badgeuse/mentions-legales.html"
    const val SOURCE_CODE_URL = "https://github.com/tikris/appc-badgeuse"
    const val SOURCE_CODE_ANDROID_URL = "https://github.com/tikris/appc-badgeuse-android"

    // MARK: Mentions légales
    const val CONTACT_EMAIL = "christophe.amalric@appc.paris"
    const val SUGGESTIONS_EMAIL = CONTACT_EMAIL
    const val LEGAL_SEAT = "6, rue Montmartre, 75001 Paris"
    const val LEGAL_REGISTRATION = "Association loi 1901 – n° RNA en cours d'attribution"
    const val PUBLICATION_DIRECTOR = "Christophe Amalric, président de l'APPC"

    // MARK: Notification de proximité (géorepérage)
    // Désactivée en version 1 : elle exige la localisation en arrière-plan, que Google Play n'accepte
    // qu'après formulaire, vidéo et avertissement visible dans l'app. Le code reste en place (GeofenceManager).
    const val PROXIMITY_NOTIFICATION = false

    // MARK: Statistiques facultatives (Matomo auto-hébergé, voir MATOMO.md)
    val STATS_ENDPOINT: String? = null
    const val STATS_SITE_ID = 1

    // MARK: Textes d'accompagnement
    const val DEFAULT_BADGE_TITLE = "Badge crèche"
    const val RELEVANT_TEXT = "Vous arrivez à la crèche : votre badge est prêt."
    const val USAGE_TEXT = "Présentez ce QR code à la badgeuse à l'arrivée et au départ. Gardez votre badge d'origine en secours."
    const val ABOUT_TEXT = "Badge créé avec l'application Badge Crèche, une initiative bénévole de l'association Parents à Paris Centre (APPC). Application indépendante, non affiliée à la Ville de Paris ni au gestionnaire de votre crèche."
    const val PRIVACY_TEXT = "Badge créé sur votre appareil Android. Le QR code et le nom de l'enfant ne quittent jamais votre téléphone et l'APPC n'y a jamais accès : vos données vous appartiennent."
}
