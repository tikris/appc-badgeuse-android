package fr.parentsapariscentre.badgecreche.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import fr.parentsapariscentre.badgecreche.service.QRCodeGenerator
import fr.parentsapariscentre.badgecreche.ui.theme.AppcSecondaryText

@Composable
fun QRCodePreview(
    payload: String?,
    modifier: Modifier = Modifier
) {
    var qrBitmap by remember(payload) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(payload) { mutableStateOf(payload != null) }

    LaunchedEffect(payload) {
        if (!payload.isNullOrBlank()) {
            isLoading = true
            qrBitmap = QRCodeGenerator.generateQrBitmap(payload, 512)
            isLoading = false
        } else {
            qrBitmap = null
            isLoading = false
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
            qrBitmap != null -> {
                Image(
                    bitmap = qrBitmap!!.asImageBitmap(),
                    contentDescription = "QR Code de la crèche",
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = AppcSecondaryText,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
