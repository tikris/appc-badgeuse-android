package fr.parentsapariscentre.badgecreche.data.model

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class CrecheStat(
    val event: Event,
    val source: Source,
    val crecheLabel: String
) {
    enum class Event(val rawValue: String) {
        CREATED("Badge créé"),
        CRECHE_CHANGED("Crèche changée")
    }

    enum class Source(val rawValue: String) {
        NEARBY("Parmi les 3 plus proches"),
        LIST("Dans la liste"),
        MANUAL("Hors liste")
    }

    constructor(event: Event, source: Source, creche: Creche?) : this(
        event = event,
        source = source,
        crecheLabel = creche?.let { "${it.name} · ${it.postcode}" } ?: Source.MANUAL.rawValue
    )

    fun trackingUrl(endpoint: String, siteId: Int): String {
        fun encode(s: String) = URLEncoder.encode(s, StandardCharsets.UTF_8.name())
        val query = listOf(
            "idsite" to siteId.toString(),
            "rec" to "1",
            "apiv" to "1",
            "send_image" to "0",
            "ca" to "1",
            "url" to "http://fr.parentsapariscentre.badgecreche/badge",
            "e_c" to event.rawValue,
            "e_a" to source.rawValue,
            "e_n" to crecheLabel
        ).joinToString("&") { "${it.first}=${encode(it.second)}" }

        val sep = if (endpoint.contains("?")) "&" else "?"
        return "$endpoint$sep$query"
    }
}
