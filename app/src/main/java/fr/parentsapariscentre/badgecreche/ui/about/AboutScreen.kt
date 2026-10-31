package fr.parentsapariscentre.badgecreche.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.service.StatsService
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral
import fr.parentsapariscentre.badgecreche.ui.theme.AppcNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    sharesStats: Boolean,
    onToggleStats: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("À propos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
                .padding(16.dp)
        ) {
            // Header icône + nom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(AppcNavy, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = AppcCoral,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(AppConfig.APP_NAME, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Version 1.0", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Initiative APPC
            AboutCard(title = "Une initiative de l'APPC") {
                Text(
                    text = "Badge Crèche est une application gratuite et sans publicité, créée bénévolement par l'association Parents à Paris Centre (APPC) pour simplifier le quotidien des familles : le QR code de la badgeuse toujours à portée de main.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vous êtes parent à Paris Centre ? Rejoignez la communauté de l'APPC pour échanger, vous entraider et suivre nos actions.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUrl(AppConfig.WHATSAPP_COMMUNITY_URL) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rejoindre la communauté WhatsApp",
                        color = AppcCoral,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Vos données vous appartiennent
            AboutCard(title = "Vos données vous appartiennent") {
                PrivacyRow(
                    icon = Icons.Default.CloudOff,
                    text = "Votre QR code, le nom de votre enfant et votre position ne quittent jamais votre téléphone : ils ne sont stockés ni dans le cloud ni sur un serveur, et l'APPC n'y a jamais accès."
                )
                PrivacyRow(
                    icon = Icons.Default.Lock,
                    text = "Vos données vous appartiennent entièrement. Vous pouvez les modifier ou les effacer à tout moment ; elles ne sont ni vendues, ni partagées."
                )
                PrivacyRow(
                    icon = Icons.Default.LocationOn,
                    text = "Votre position sert uniquement, au moment de créer le badge, à trier les crèches par distance. Le calcul se fait sur votre téléphone : la position n'est ni enregistrée ni transmise."
                )

                if (StatsService.isAvailable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Partager le nom de ma crèche, de façon anonyme", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Facultatif, désactivé par défaut. Si activé, seuls le nom de la crèche et la façon dont vous l'avez choisie sont envoyés aux statistiques de l'APPC (Matomo), une fois à l'enregistrement.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = sharesStats, onCheckedChange = onToggleStats)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Politique de confidentialité",
                    color = AppcCoral,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { openUrl(AppConfig.PRIVACY_POLICY_URL) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Mentions légales
            AboutCard(title = "Mentions légales") {
                LegalField("Éditeur", "${AppConfig.ASSOCIATION_NAME} (${AppConfig.ASSOCIATION_SHORT_NAME})")
                LegalField("Statut", AppConfig.LEGAL_REGISTRATION)
                LegalField("Siège", AppConfig.LEGAL_SEAT)
                LegalField("Directeur de la publication", AppConfig.PUBLICATION_DIRECTOR)
                LegalField("Liste des crèches", "Établissements issus de l'annuaire de la Ville de Paris (paris.fr), de l'Apur (Atelier parisien d'urbanisme, licence ODbL) et d'OpenStreetMap (© contributeurs OpenStreetMap, licence ODbL).")

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Mentions légales complètes",
                    color = AppcCoral,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { openUrl(AppConfig.LEGAL_NOTICE_URL) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Code source (licence GPL v3)",
                    color = AppcCoral,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { openUrl(AppConfig.SOURCE_CODE_ANDROID_URL) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun PrivacyRow(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = AppcCoral, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LegalField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp)
    }
}
