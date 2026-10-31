package fr.parentsapariscentre.badgecreche.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.MainActivity
import fr.parentsapariscentre.badgecreche.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                showProximityNotification(context)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                dismissProximityNotification(context)
            }
        }
    }

    private fun showProximityNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "creche_proximity_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Arrivée à la crèche",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Affiche le badge sur l'écran verrouillé à proximité de la crèche"
                enableVibration(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_tile_badge)
            .setContentTitle(AppConfig.APP_NAME)
            .setContentText(AppConfig.RELEVANT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun dismissProximityNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }

    companion object {
        const val ACTION_GEOFENCE_EVENT = "fr.parentsapariscentre.badgecreche.ACTION_GEOFENCE_EVENT"
        const val NOTIFICATION_ID = 42
    }
}
