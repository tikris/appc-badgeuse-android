package fr.parentsapariscentre.badgecreche.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import fr.parentsapariscentre.badgecreche.data.model.Creche
import fr.parentsapariscentre.badgecreche.data.model.CrecheAddress
import fr.parentsapariscentre.badgecreche.data.model.CrecheStat
import fr.parentsapariscentre.badgecreche.data.model.LockScreenDistance
import fr.parentsapariscentre.badgecreche.service.StatsService
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeEditorScreen(
    existingBadge: BadgeData?,
    sharesStats: Boolean,
    onSave: (BadgeData, CrecheStat?) -> Unit,
    onCancel: () -> Unit,
    onLaunchCamera: () -> Unit,
    onOpenCrecheList: () -> Unit
) {
    var qrPayload by remember { mutableStateOf(existingBadge?.qrPayload) }
    var address by remember { mutableStateOf<CrecheAddress?>(existingBadge?.address) }
    var lockScreenDistance by remember { mutableStateOf(existingBadge?.lockScreenDistance ?: LockScreenDistance.STANDARD) }

    var showsCrecheName by remember { mutableStateOf(existingBadge == null || existingBadge.crecheName.isNotEmpty()) }
    var crecheName by remember { mutableStateOf(existingBadge?.crecheName ?: (existingBadge?.address?.name ?: "")) }

    var showsChildName by remember { mutableStateOf(existingBadge?.showsChildName ?: false) }
    var childFirstName by remember { mutableStateOf(existingBadge?.childFirstName ?: "") }
    var childLastName by remember { mutableStateOf(existingBadge?.childLastName ?: "") }

    var crecheChoiceStat by remember { mutableStateOf<Pair<CrecheStat.Source, Creche?>?>(null) }
    var currentSharesStats by remember { mutableStateOf(sharesStats) }

    val canSave = !qrPayload.isNullOrBlank() && address != null && address!!.label.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingBadge == null) "Nouveau badge" else "Modifier le badge") },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Annuler")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val addr = address ?: return@TextButton
                            val payload = qrPayload ?: return@TextButton

                            val fName = childFirstName.trim()
                            val lName = childLastName.trim()
                            val keepsChildName = showsChildName && (fName.isNotEmpty() || lName.isNotEmpty())

                            val updatedBadge = BadgeData(
                                serialNumber = existingBadge?.serialNumber ?: java.util.UUID.randomUUID().toString(),
                                qrPayload = payload,
                                showsChildName = keepsChildName,
                                childFirstName = if (keepsChildName) fName else "",
                                childLastName = if (keepsChildName) lName else "",
                                crecheName = if (showsCrecheName) crecheName.trim() else "",
                                address = addr,
                                updatedAt = System.currentTimeMillis(),
                                lockScreenMeters = lockScreenDistance.rawValue
                            )

                            val stat = if (crecheChoiceStat != null && existingBadge?.address != addr) {
                                CrecheStat(
                                    event = if (existingBadge == null) CrecheStat.Event.CREATED else CrecheStat.Event.CRECHE_CHANGED,
                                    source = crecheChoiceStat!!.first,
                                    creche = crecheChoiceStat!!.second
                                )
                            } else null

                            onSave(updatedBadge, stat)
                        },
                        enabled = canSave
                    ) {
                        Text(
                            text = "Enregistrer",
                            fontWeight = FontWeight.Bold,
                            color = if (canSave) AppcCoral else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Choix de la crèche
            CrecheSection(
                address = if (address?.label?.isEmpty() == true) null else address,
                lockScreenDistance = lockScreenDistance,
                onAddressSelected = { selectedAddress, selectedName, source, creche ->
                    address = selectedAddress
                    if (selectedName.isNotEmpty() && crecheName.isEmpty()) {
                        crecheName = selectedName
                    }
                    crecheChoiceStat = source to creche
                },
                onDistanceChanged = { lockScreenDistance = it },
                onOpenCrecheList = onOpenCrecheList
            )

            // Section QR Code
            QRCodeSection(
                payload = qrPayload,
                onPayloadChanged = { qrPayload = it },
                onLaunchCamera = onLaunchCamera
            )

            // Section Affichage sur le badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sur le badge (facultatif)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Nom de la crèche
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Afficher le nom de la crèche",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = showsCrecheName,
                            onCheckedChange = { showsCrecheName = it }
                        )
                    }

                    AnimatedVisibility(visible = showsCrecheName) {
                        OutlinedTextField(
                            value = crecheName,
                            onValueChange = { crecheName = it },
                            label = { Text("Nom de la crèche") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Nom de l'enfant
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Afficher le nom de l'enfant",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = showsChildName,
                            onCheckedChange = { showsChildName = it }
                        )
                    }

                    AnimatedVisibility(visible = showsChildName) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = childFirstName,
                                onValueChange = { childFirstName = it },
                                label = { Text("Prénom") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = childLastName,
                                onValueChange = { childLastName = it },
                                label = { Text("Nom (facultatif)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Text(
                        text = "Uniquement si vous le souhaitez. Ces informations restent sur votre smartphone : elles ne sont jamais transmises à l'APPC.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Section Statistiques facultatives (si serveur configuré)
            if (StatsService.isAvailable) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Aider l'APPC (facultatif)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Partager le nom de ma crèche, de façon anonyme",
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp
                            )
                            Switch(
                                checked = currentSharesStats,
                                onCheckedChange = { currentSharesStats = it }
                            )
                        }
                        Text(
                            text = "Pour savoir dans quelles crèches l'application est utilisée. Seuls le nom de la crèche et la façon dont vous l'avez choisie sont envoyés, une fois, à l'enregistrement : jamais le QR code, le nom de l'enfant ni votre position.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
