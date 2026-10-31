package fr.parentsapariscentre.badgecreche.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.badgeDataStore: DataStore<Preferences> by preferencesDataStore(name = "badge_prefs")

class BadgeRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val badgeKey = stringPreferencesKey("badge_data")
    private val consentStatsKey = stringPreferencesKey("shares_creche_stats")

    val badgeFlow: Flow<BadgeData?> = context.badgeDataStore.data.map { preferences ->
        preferences[badgeKey]?.let { serialized ->
            try {
                json.decodeFromString<BadgeData>(serialized)
            } catch (e: Exception) {
                null
            }
        }
    }

    val sharesStatsFlow: Flow<Boolean> = context.badgeDataStore.data.map { preferences ->
        preferences[consentStatsKey]?.toBoolean() ?: false
    }

    suspend fun saveBadge(badge: BadgeData) {
        val serialized = json.encodeToString(badge)
        context.badgeDataStore.edit { preferences ->
            preferences[badgeKey] = serialized
        }
    }

    suspend fun deleteBadge() {
        context.badgeDataStore.edit { preferences ->
            preferences.remove(badgeKey)
        }
    }

    suspend fun setSharesStats(enabled: Boolean) {
        context.badgeDataStore.edit { preferences ->
            preferences[consentStatsKey] = enabled.toString()
        }
    }
}
