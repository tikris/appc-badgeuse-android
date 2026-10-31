package fr.parentsapariscentre.badgecreche.ui.editor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.AppConfig
import fr.parentsapariscentre.badgecreche.data.model.CrecheAddress
import fr.parentsapariscentre.badgecreche.data.model.CrecheNameSuggestion
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral

@Composable
fun CrecheNameSuggestionDialog(
    address: CrecheAddress,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var proposedName by remember { mutableStateOf("") }

    val suggestion = remember(proposedName, address) {
        CrecheNameSuggestion(
            proposedName = proposedName,
            currentName = address.name,
            address = address.label,
            crecheId = null
        )
    }

    fun send() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = suggestion.mailtoUri(AppConfig.SUGGESTIONS_EMAIL)
        }
        try {
            context.startActivity(emailIntent)
            onDismiss()
        } catch (e: Exception) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Suggestion de nom", suggestion.body))
            Toast.makeText(context, "Aucune messagerie. Message copié dans le presse-papier !", Toast.LENGTH_LONG).show()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Proposer un nom d'usage", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = address.name ?: "Sans nom",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = address.label,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = proposedName,
                    onValueChange = { proposedName = it },
                    label = { Text("Nom utilisé par les parents") },
                    placeholder = { Text("ex. Halte-garderie Montmartre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Votre messagerie s'ouvre avec un e-mail prêt à envoyer à l'APPC, qui met à jour la liste pour tous les parents dans une prochaine version.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = ::send,
                enabled = suggestion.isValid
            ) {
                Text("Envoyer", color = AppcCoral, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
