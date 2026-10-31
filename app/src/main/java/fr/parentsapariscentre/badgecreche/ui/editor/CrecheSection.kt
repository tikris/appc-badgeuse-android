package fr.parentsapariscentre.badgecreche.ui.editor

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.parentsapariscentre.badgecreche.data.model.Creche
import fr.parentsapariscentre.badgecreche.data.model.CrecheAddress
import fr.parentsapariscentre.badgecreche.data.model.CrecheStat
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.data.model.LockScreenDistance
import fr.parentsapariscentre.badgecreche.data.model.NearbyCreche
import fr.parentsapariscentre.badgecreche.data.repository.CrecheCatalogRepository
import fr.parentsapariscentre.badgecreche.service.LocationProvider
import fr.parentsapariscentre.badgecreche.ui.components.AddressMap
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral
import kotlinx.coroutines.launch

@Composable
fun CrecheSection(
    address: CrecheAddress?,
    lockScreenDistance: LockScreenDistance,
    onAddressSelected: (CrecheAddress, String, CrecheStat.Source, Creche?) -> Unit,
    onDistanceChanged: (LockScreenDistance) -> Unit,
    onOpenCrecheList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLocating by remember { mutableStateOf(false) }
    var nearbyCreches by remember { mutableStateOf<List<NearbyCreche>?>(null) }
    var showLockScreenDialog by remember { mutableStateOf(false) }
    var showSuggestionDialog by remember { mutableStateOf(false) }

    fun fetchNearby() {
        isLocating = true
        scope.launch {
            val location = LocationProvider(context).getCurrentLocation()
            if (location != null) {
                val dir = CrecheCatalogRepository.getInstance(context).getDirectory()
                nearbyCreches = dir.nearest(location.latitude, location.longitude, 3)
            }
            isLocating = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchNearby()
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission && address == null) {
            fetchNearby()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Votre crèche",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (address != null) {
                // Crèche sélectionnée : Carte + Coordonnées + Réglages
                AddressMap(
                    address = address,
                    distance = if (AppConfig.PROXIMITY_NOTIFICATION) lockScreenDistance else LockScreenDistance.NEVER,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Column {
                    Text(
                        text = address.name ?: "Votre crèche",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = address.label,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Bouton réglage de l'écran verrouillé (notification de proximité, désactivée en version 1)
                if (AppConfig.PROXIMITY_NOTIFICATION) {
                    OutlinedButton(
                        onClick = { showLockScreenDialog = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Écran verrouillé : ${lockScreenDistance.title}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Bouton proposer nom d'usage
                OutlinedButton(
                    onClick = { showSuggestionDialog = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Proposer le nom utilisé par les parents",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = {
                        onAddressSelected(
                            CrecheAddress("", 0.0, 0.0),
                            "",
                            CrecheStat.Source.LIST,
                            null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Choisir une autre crèche")
                }

            } else {
                // Pas encore de crèche sélectionnée : Suggestions autour de moi + Liste
                if (isLocating) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Recherche des crèches proches…", fontSize = 13.sp)
                    }
                } else if (!nearbyCreches.isNullOrEmpty()) {
                    Text(
                        text = "Crèches à proximité :",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    nearbyCreches?.forEach { item ->
                        CrecheListItem(
                            creche = item.creche,
                            distanceMeters = item.distanceMeters,
                            onClick = {
                                onAddressSelected(
                                    item.creche.badgeAddress,
                                    item.creche.name,
                                    CrecheStat.Source.NEARBY,
                                    item.creche
                                )
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                fetchNearby()
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trouver les crèches autour de moi",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenCrecheList,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Toutes les crèches de Paris",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Votre position sert uniquement à trier les crèches, sur votre téléphone : elle n'est ni enregistrée ni transmise.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showLockScreenDialog && address != null) {
        LockScreenSettingsDialog(
            address = address,
            initialDistance = lockScreenDistance,
            onSave = { updatedAddress, updatedDistance ->
                onDistanceChanged(updatedDistance)
                onAddressSelected(updatedAddress, updatedAddress.name ?: "", CrecheStat.Source.LIST, null)
            },
            onDismiss = { showLockScreenDialog = false }
        )
    }

    if (showSuggestionDialog && address != null) {
        CrecheNameSuggestionDialog(
            address = address,
            onDismiss = { showSuggestionDialog = false }
        )
    }
}
