package fr.parentsapariscentre.badgecreche.data.model

import android.net.Uri

data class CrecheNameSuggestion(
    val proposedName: String,
    val currentName: String?,
    val address: String,
    val crecheId: String?
) {
    val isValid: Boolean
        get() = proposedName.trim().isNotEmpty()

    val subject: String
        get() = "Nom d'usage : ${currentName ?: address}"

    val body: String
        get() = buildString {
            append("Bonjour l'APPC,\n\n")
            append("Voici le nom d'usage de ma crèche à intégrer dans la prochaine version de l'application Badge Crèche :\n\n")
            append("• Nom utilisé par les parents : ").append(proposedName.trim()).append("\n")
            append("• Nom actuel dans l'app : ").append(currentName ?: "Sans nom").append("\n")
            append("• Adresse : ").append(address).append("\n")
            if (crecheId != null) {
                append("• Identifiant : ").append(crecheId).append("\n")
            }
            append("\nMerci !")
        }

    fun mailtoUri(toEmail: String): Uri {
        return Uri.parse("mailto:$toEmail")
            .buildUpon()
            .appendQueryParameter("subject", subject)
            .appendQueryParameter("body", body)
            .build()
    }
}
