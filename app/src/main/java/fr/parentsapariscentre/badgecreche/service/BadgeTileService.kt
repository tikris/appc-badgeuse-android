package fr.parentsapariscentre.badgecreche.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import fr.parentsapariscentre.badgecreche.MainActivity
import fr.parentsapariscentre.badgecreche.data.repository.BadgeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BadgeTileService : TileService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        scope.launch {
            val badge = BadgeRepository(applicationContext).badgeFlow.firstOrNull()
            if (badge != null) {
                tile.state = Tile.STATE_ACTIVE
                tile.label = badge.displayedCrecheName ?: "Badge Crèche"
            } else {
                tile.state = Tile.STATE_INACTIVE
                tile.label = "Badge Crèche"
            }
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
