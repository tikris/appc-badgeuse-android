package fr.parentsapariscentre.badgecreche.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import fr.parentsapariscentre.badgecreche.ui.theme.AppcLabel
import fr.parentsapariscentre.badgecreche.ui.theme.AppcNavy

@Composable
fun BadgeCard(
    badge: BadgeData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Carte du badge de crèche" },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppcNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // En-tête : Icône maison + Titre
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = AppConfig.DEFAULT_BADGE_TITLE,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Champs Nom de l'enfant et Nom de la crèche
            val childName = badge.displayedChildName
            val crecheName = badge.displayedCrecheName

            if (childName != null) {
                BadgeField(label = "ENFANT", value = childName, prominent = true)
                if (crecheName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    BadgeField(label = "CRÈCHE", value = crecheName, prominent = false)
                }
            } else {
                BadgeField(
                    label = "BADGE",
                    value = crecheName ?: AppConfig.DEFAULT_BADGE_TITLE,
                    prominent = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pavé blanc QR Code
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    QRCodePreview(
                        payload = badge.qrPayload,
                        modifier = Modifier.size(160.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeField(
    label: String,
    value: String,
    prominent: Boolean
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppcLabel,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = if (prominent) 20.sp else 15.sp,
            fontWeight = if (prominent) FontWeight.SemiBold else FontWeight.Normal,
            color = Color.White
        )
    }
}
