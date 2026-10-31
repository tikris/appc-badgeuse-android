package fr.parentsapariscentre.badgecreche.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Creche(
    val id: String,
    val name: String,
    val kind: String,
    val management: String? = null,
    val street: String,
    val postcode: String,
    val lat: Double,
    val lon: Double
) {
    val fullAddress: String
        get() = if (postcode.isEmpty()) "$street, Paris" else "$street, $postcode Paris"

    val arrondissementNumber: Int
        get() {
            if (!postcode.startsWith("75")) return 0
            val suffix = postcode.takeLast(3)
            val num = suffix.toIntOrNull() ?: return 0
            return if (num == 116) 16 else num
        }

    val arrondissement: String
        get() = when (arrondissementNumber) {
            0 -> "Paris"
            1 -> "1er arrondissement"
            else -> "${arrondissementNumber}e arrondissement"
        }

    val subtitle: String
        get() {
            val category = listOfNotNull(kind, management).joinToString(" ")
            if (management != null && CrecheDirectory.fold(name).startsWith(CrecheDirectory.fold(category))) {
                return fullAddress
            }
            return if (category.isEmpty()) fullAddress else "$category · $fullAddress"
        }

    val badgeAddress: CrecheAddress
        get() = CrecheAddress(
            label = fullAddress,
            latitude = lat,
            longitude = lon,
            name = name
        )
}

data class NearbyCreche(
    val creche: Creche,
    val distanceMeters: Double
)
