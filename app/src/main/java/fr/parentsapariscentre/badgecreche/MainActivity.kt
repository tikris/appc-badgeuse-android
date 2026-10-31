package fr.parentsapariscentre.badgecreche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.updateAll
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import fr.parentsapariscentre.badgecreche.data.model.Creche
import fr.parentsapariscentre.badgecreche.data.repository.BadgeRepository
import fr.parentsapariscentre.badgecreche.service.GeofenceManager
import fr.parentsapariscentre.badgecreche.service.StatsService
import fr.parentsapariscentre.badgecreche.ui.about.AboutScreen
import fr.parentsapariscentre.badgecreche.ui.editor.BadgeEditorScreen
import fr.parentsapariscentre.badgecreche.ui.editor.CrecheListScreen
import fr.parentsapariscentre.badgecreche.ui.home.BadgeHomeScreen
import fr.parentsapariscentre.badgecreche.ui.scanner.CameraScannerScreen
import fr.parentsapariscentre.badgecreche.ui.theme.BadgeCrecheTheme
import fr.parentsapariscentre.badgecreche.ui.welcome.WelcomeScreen
import fr.parentsapariscentre.badgecreche.widget.BadgeAppWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: BadgeRepository
    private lateinit var geofenceManager: GeofenceManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = BadgeRepository(applicationContext)
        geofenceManager = GeofenceManager(applicationContext)

        setContent {
            BadgeCrecheTheme {
                val scope = rememberCoroutineScope()
                val badge by repository.badgeFlow.collectAsState(initial = null)
                val sharesStats by repository.sharesStatsFlow.collectAsState(initial = false)

                var isEditing by remember { mutableStateOf(false) }
                var isShowingAbout by remember { mutableStateOf(false) }
                var isScanning by remember { mutableStateOf(false) }
                var isSelectingFromList by remember { mutableStateOf(false) }
                var temporarilySelectedCreche by remember { mutableStateOf<Creche?>(null) }
                var scannedQrPayload by remember { mutableStateOf<String?>(null) }

                fun saveBadge(newBadge: BadgeData) {
                    scope.launch {
                        repository.saveBadge(newBadge)
                        if (AppConfig.PROXIMITY_NOTIFICATION) geofenceManager.updateGeofence(newBadge)
                        BadgeAppWidget().updateAll(applicationContext)
                        isEditing = false
                    }
                }

                fun deleteBadge() {
                    scope.launch {
                        repository.deleteBadge()
                        if (AppConfig.PROXIMITY_NOTIFICATION) geofenceManager.updateGeofence(null)
                        BadgeAppWidget().updateAll(applicationContext)
                    }
                }

                when {
                    isScanning -> {
                        CameraScannerScreen(
                            onScanned = { payload ->
                                scannedQrPayload = payload
                                isScanning = false
                            },
                            onClose = { isScanning = false }
                        )
                    }
                    isSelectingFromList -> {
                        CrecheListScreen(
                            onSelect = { creche ->
                                temporarilySelectedCreche = creche
                                isSelectingFromList = false
                            },
                            onBack = { isSelectingFromList = false }
                        )
                    }
                    isShowingAbout -> {
                        AboutScreen(
                            sharesStats = sharesStats,
                            onToggleStats = { enabled ->
                                scope.launch { repository.setSharesStats(enabled) }
                            },
                            onBack = { isShowingAbout = false }
                        )
                    }
                    isEditing -> {
                        val baseBadge = if (scannedQrPayload != null || temporarilySelectedCreche != null) {
                            (badge ?: BadgeData(qrPayload = "", address = fr.parentsapariscentre.badgecreche.data.model.CrecheAddress("", 0.0, 0.0))).copy(
                                qrPayload = scannedQrPayload ?: (badge?.qrPayload ?: ""),
                                address = temporarilySelectedCreche?.badgeAddress ?: (badge?.address ?: fr.parentsapariscentre.badgecreche.data.model.CrecheAddress("", 0.0, 0.0)),
                                crecheName = temporarilySelectedCreche?.name ?: (badge?.crecheName ?: "")
                            )
                        } else {
                            badge
                        }

                        BadgeEditorScreen(
                            existingBadge = baseBadge,
                            sharesStats = sharesStats,
                            onSave = { updated, stat ->
                                saveBadge(updated)
                                if (stat != null && sharesStats) {
                                    scope.launch { StatsService.sendStat(stat) }
                                }
                                scannedQrPayload = null
                                temporarilySelectedCreche = null
                            },
                            onCancel = {
                                isEditing = false
                                scannedQrPayload = null
                                temporarilySelectedCreche = null
                            },
                            onLaunchCamera = { isScanning = true },
                            onOpenCrecheList = { isSelectingFromList = true }
                        )
                    }
                    else -> {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text(AppConfig.APP_NAME) },
                                    actions = {
                                        IconButton(onClick = { isShowingAbout = true }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = "À propos"
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                                )
                            }
                        ) { paddingValues ->
                            AnimatedContent(
                                targetState = badge,
                                label = "home_content",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) { currentBadge ->
                                if (currentBadge != null) {
                                    BadgeHomeScreen(
                                        badge = currentBadge,
                                        onEdit = { isEditing = true },
                                        onDelete = ::deleteBadge
                                    )
                                } else {
                                    WelcomeScreen(
                                        onStart = { isEditing = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
