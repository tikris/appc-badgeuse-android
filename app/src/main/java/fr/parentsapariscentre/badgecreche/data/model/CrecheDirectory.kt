package fr.parentsapariscentre.badgecreche.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.Normalizer
import kotlin.math.*

@Serializable
private data class CrecheDocument(
    val source: String,
    val licence: String,
    val generated: String,
    val creches: List<Creche>
)

class CrecheDirectory(
    val creches: List<Creche>,
    val source: String,
    val licence: String,
    val generated: String
) {
    private val searchKeys: List<String> = creches.map { creche ->
        fold("${creche.name} ${creche.street} ${creche.postcode} ${creche.arrondissement} ${creche.kind}")
    }

    fun nearest(latitude: Double, longitude: Double, count: Int = 3): List<NearbyCreche> {
        val result = mutableListOf<NearbyCreche>()
        for (creche in creches) {
            val d = distance(latitude, longitude, creche.lat, creche.lon)
            if (result.size < count || d < result.last().distanceMeters) {
                val index = result.indexOfFirst { d < it.distanceMeters }.let { if (it == -1) result.size else it }
                result.add(index, NearbyCreche(creche, d))
                if (result.size > count) {
                    result.removeAt(result.size - 1)
                }
            }
        }
        return result
    }

    fun search(query: String): List<Creche> {
        val terms = fold(query).split(" ").filter { it.isNotEmpty() }
        if (terms.isEmpty()) return creches

        val significant = terms.filter { it !in optionalTerms }
        val required = if (significant.isEmpty()) terms else significant

        val complete = mutableListOf<Creche>()
        val partial = mutableListOf<Creche>()

        for (i in creches.indices) {
            val creche = creches[i]
            val key = searchKeys[i]
            if (required.all { keyContains(key, it) }) {
                if (terms.all { keyContains(key, it) }) {
                    complete.add(creche)
                } else {
                    partial.add(creche)
                }
            }
        }
        return complete + partial
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun parse(jsonString: String): CrecheDirectory {
            val doc = json.decodeFromString<CrecheDocument>(jsonString)
            return CrecheDirectory(
                creches = doc.creches,
                source = doc.source,
                licence = doc.licence,
                generated = doc.generated
            )
        }

        fun byArrondissement(creches: List<Creche>): List<Pair<String, List<Creche>>> {
            return creches.groupBy { it.arrondissementNumber }
                .entries
                .sortedBy { it.key }
                .map { entry ->
                    val title = entry.value.firstOrNull()?.arrondissement ?: "Paris"
                    title to entry.value
                }
        }

        fun fold(text: String): String {
            val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            val withoutAccents = normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            return withoutAccents.lowercase()
                .replace("[-'’,.()&/]".toRegex(), " ")
                .trim()
                .replace("\\s+".toRegex(), " ")
        }

        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val radius = 6371000.0 // Rayon de la Terre en mètres
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return radius * c
        }

        private val optionalTerms: Set<String> = setOf(
            "creche", "creches", "halte", "haltes", "garderie", "garderies", "multi", "accueil", "micro",
            "jardin", "jardins", "enfant", "enfants", "municipale", "municipal", "associative", "associatif",
            "parentale", "familiale", "collective", "de", "du", "des", "d", "la", "le", "les", "l", "aux", "au", "et"
        )

        private fun keyContains(key: String, term: String): Boolean {
            if (key.contains(term)) return true
            if (term.length > 3 && (term.endsWith("s") || term.endsWith("x"))) {
                return key.contains(term.dropLast(1))
            }
            return false
        }
    }
}
