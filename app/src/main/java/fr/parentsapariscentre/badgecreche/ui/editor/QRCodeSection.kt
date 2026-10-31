package fr.parentsapariscentre.badgecreche.ui.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.service.QRCodeDecoder
import fr.parentsapariscentre.badgecreche.ui.components.QRCodePreview
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral
import kotlinx.coroutines.launch

@Composable
fun QRCodeSection(
    payload: String?,
    onPayloadChanged: (String) -> Unit,
    onLaunchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isReading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun processUri(uri: Uri?) {
        if (uri == null) return
        isReading = true
        scope.launch {
            when (val result = QRCodeDecoder.decodeFromUri(context, uri)) {
                is QRCodeDecoder.DecodeResult.Success -> {
                    onPayloadChanged(result.payload)
                }
                is QRCodeDecoder.DecodeResult.NoQrCode -> {
                    errorMessage = "Aucun QR code détecté. Essayez avec une image plus nette ou recadrée."
                }
                is QRCodeDecoder.DecodeResult.Error -> {
                    errorMessage = result.message
                }
            }
            isReading = false
        }
    }

    // Photo picker (Android 13+ Photo Picker standard et rétrocompatible)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        processUri(uri)
    }

    // File picker (PDF ou image)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        processUri(uri)
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
                text = "QR code de la crèche",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Aperçu si déjà scanné
            if (!payload.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        QRCodePreview(payload = payload)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "QR code prêt",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = payload,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isReading) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Lecture du QR code…", fontSize = 13.sp)
                }
            }

            // Bouton Scan caméra
            OutlinedButton(
                onClick = onLaunchCamera,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (payload == null) "Scanner le QR code" else "Scanner à nouveau",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Bouton Photo / Capture d'écran
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Choisir une photo ou capture d'écran",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Bouton Importer un fichier
            OutlinedButton(
                onClick = {
                    filePickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Importer un fichier (PDF, image)",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (payload == null) {
                TextButton(
                    onClick = { onPayloadChanged("APPC-DEMO-BADGE-0001") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Utiliser le QR code de test (démo)",
                        fontSize = 12.sp,
                        color = AppcCoral
                    )
                }
            }

            Text(
                text = "Le QR code remis par la crèche en début d'année. Il est reproduit à l'identique dans votre badge.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Oups") },
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
