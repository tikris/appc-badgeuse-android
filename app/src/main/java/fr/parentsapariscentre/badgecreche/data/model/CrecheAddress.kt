package fr.parentsapariscentre.badgecreche.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CrecheAddress(
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
)
