package fr.parentsapariscentre.badgecreche.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class LockScreenDistance(val rawValue: Int) {
    DOOR(10),
    STREET(25),
    APPROACH(50),
    NEVER(0);

    val meters: Int?
        get() = if (this == NEVER) null else rawValue

    val title: String
        get() = meters?.let { "$it m" } ?: "Jamais"

    val explanation: String
        get() = when (this) {
            DOOR -> "Le plus près possible. En ville, la position varie de quelques dizaines de mètres. Badge proposé trop tôt ? Placez le repère 20 à 30 m au-delà de la porte."
            STREET -> "Le badge apparaît en arrivant dans la rue de la crèche."
            APPROACH -> "Le badge apparaît à l'approche de la crèche : plus tôt, mais plus régulièrement."
            NEVER -> "Le badge ne s'affiche plus tout seul. Vous pouvez toujours l'ouvrir via l'application, le widget ou la tuile rapide."
        }

    companion object {
        val STANDARD = DOOR

        fun fromRawValue(value: Int?): LockScreenDistance {
            if (value == null) return STANDARD
            return entries.firstOrNull { it.rawValue == value } ?: STANDARD
        }
    }
}
