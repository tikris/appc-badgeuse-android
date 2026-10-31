package fr.parentsapariscentre.badgecreche.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import kotlinx.coroutines.tasks.await

class GeofenceManager(private val context: Context) {
    private val client = LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun updateGeofence(badge: BadgeData?) {
        try {
            client.removeGeofences(pendingIntent).await()
        } catch (e: Exception) {
            // Pas de geofence préalable
        }

        if (badge == null) return
        val meters = badge.lockScreenDistance.meters ?: return

        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(
                badge.address.latitude,
                badge.address.longitude,
                meters.toFloat().coerceAtLeast(15f) // Au moins 15 m pour la sensibilité GPS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        try {
            client.addGeofences(request, pendingIntent).await()
        } catch (e: Exception) {
            // Permission de localisation en arrière-plan non accordée ou Play Services indisponibles
        }
    }

    companion object {
        const val GEOFENCE_ID = "creche_proximity_geofence"
    }
}
