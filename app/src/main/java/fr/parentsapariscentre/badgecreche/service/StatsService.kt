package fr.parentsapariscentre.badgecreche.service

import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.data.model.CrecheStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object StatsService {

    val isAvailable: Boolean
        get() = AppConfig.STATS_ENDPOINT != null

    suspend fun sendStat(stat: CrecheStat) = withContext(Dispatchers.IO) {
        val endpoint = AppConfig.STATS_ENDPOINT ?: return@withContext
        try {
            val trackingUrl = stat.trackingUrl(endpoint, AppConfig.STATS_SITE_ID)
            val connection = URL(trackingUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.responseCode
            connection.disconnect()
        } catch (e: Exception) {
            // Échec silencieux : les statistiques ne doivent jamais bloquer l'usage
        }
    }
}
