package fr.parentsapariscentre.badgecreche.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.MainActivity
import fr.parentsapariscentre.badgecreche.data.model.BadgeData
import fr.parentsapariscentre.badgecreche.data.repository.BadgeRepository
import fr.parentsapariscentre.badgecreche.service.QRCodeGenerator
import kotlinx.coroutines.flow.firstOrNull

class BadgeAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val badge = BadgeRepository(context).badgeFlow.firstOrNull()
        val qrBitmap = badge?.let { QRCodeGenerator.generateQrBitmap(it.qrPayload, 300) }

        provideContent {
            GlanceTheme {
                WidgetContent(badge, qrBitmap)
            }
        }
    }

    @Composable
    private fun WidgetContent(badge: BadgeData?, qrBitmap: android.graphics.Bitmap?) {
        val navyColor = Color(0xFF22305A)
        val coralColor = Color(0xFFFF7A59)
        val labelColor = Color(0xFFFFB096)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(navyColor)
                .cornerRadius(16.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badge == null || qrBitmap == null) {
                Text(
                    text = AppConfig.DEFAULT_BADGE_TITLE,
                    style = TextStyle(color = androidx.glance.unit.ColorProvider(coralColor), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(GlanceModifier.height(8.dp))
                Text(
                    text = "Touchez pour configurer votre badge",
                    style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 12.sp)
                )
            } else {
                Text(
                    text = badge.displayedCrecheName ?: AppConfig.DEFAULT_BADGE_TITLE,
                    style = TextStyle(color = androidx.glance.unit.ColorProvider(Color.White), fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1
                )

                if (badge.displayedChildName != null) {
                    Text(
                        text = badge.displayedChildName ?: "",
                        style = TextStyle(color = androidx.glance.unit.ColorProvider(labelColor), fontSize = 12.sp),
                        maxLines = 1
                    )
                }

                Spacer(GlanceModifier.height(6.dp))

                Box(
                    modifier = GlanceModifier
                        .background(Color.White)
                        .cornerRadius(8.dp)
                        .padding(6.dp)
                ) {
                    Image(
                        provider = ImageProvider(qrBitmap),
                        contentDescription = "QR Code de crèche",
                        modifier = GlanceModifier.size(110.dp)
                    )
                }
            }
        }
    }
}
