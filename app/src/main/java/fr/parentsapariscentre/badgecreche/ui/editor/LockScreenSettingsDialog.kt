package fr.parentsapariscentre.badgecreche.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.data.model.CrecheAddress
import fr.parentsapariscentre.badgecreche.data.model.LockScreenDistance
import fr.parentsapariscentre.badgecreche.ui.components.AddressMap
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral
import fr.parentsapariscentre.badgecreche.ui.theme.AppcNavy

@Composable
fun LockScreenSettingsDialog(
    address: CrecheAddress,
    initialDistance: LockScreenDistance,
    onSave: (CrecheAddress, LockScreenDistance) -> Unit,
    onDismiss: () -> Unit
) {
    var distance by remember { mutableStateOf(initialDistance) }
    var lat by remember { mutableDoubleStateOf(address.latitude) }
    var lon by remember { mutableDoubleStateOf(address.longitude) }

    val currentAddress = address.copy(latitude = lat, longitude = lon)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Écran verrouillé & proximité", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Repère pas tout à fait sur la porte ? Touchez la carte à l'endroit exact de l'entrée.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Carte interactive
                AddressMap(
                    address = currentAddress,
                    distance = distance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    isInteractive = true,
                    onCoordinateChanged = { newLat, newLon ->
                        lat = newLat
                        lon = newLon
                    }
                )

                Text(
                    text = "Proposer le badge à :",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                // Sélecteur de distance (10m, 25m, 50m, Jamais)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LockScreenDistance.entries.forEach { option ->
                        val isSelected = option == distance
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) AppcNavy else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { distance = option }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text(
                    text = distance.explanation,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(currentAddress, distance)
                    onDismiss()
                }
            ) {
                Text("OK", color = AppcCoral, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
