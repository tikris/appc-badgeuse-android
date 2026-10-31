package fr.parentsapariscentre.badgecreche.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.parentsapariscentre.badgecreche.data.model.CrecheAddress
import fr.parentsapariscentre.badgecreche.data.model.LockScreenDistance
import fr.parentsapariscentre.badgecreche.ui.theme.AppcCoral

@Composable
fun AddressMap(
    address: CrecheAddress,
    distance: LockScreenDistance = LockScreenDistance.STANDARD,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = false,
    onCoordinateChanged: ((latitude: Double, longitude: Double) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8ECEF))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isInteractive) {
                    if (isInteractive && onCoordinateChanged != null) {
                        detectTapGestures { tapOffset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val deltaX = (tapOffset.x - centerX) / (size.width / 2f)
                            val deltaY = (tapOffset.y - centerY) / (size.height / 2f)

                            // Ajustement d'environ ~50m maximum à l'échelle
                            val deltaLat = -deltaY * 0.00045
                            val deltaLon = deltaX * 0.00065
                            onCoordinateChanged(address.latitude + deltaLat, address.longitude + deltaLon)
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Trame de fond stylisée (carte urbaine)
            val gridColor = Color(0xFFD6DBDF)
            val step = 40f
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += step
            }

            // Rayon d'affichage écran verrouillé (10m = ~35px, 25m = ~65px, 50m = ~110px)
            val meters = distance.meters
            if (meters != null) {
                val radiusPx = when (distance) {
                    LockScreenDistance.DOOR -> 35f
                    LockScreenDistance.STREET -> 65f
                    LockScreenDistance.APPROACH -> 110f
                    LockScreenDistance.NEVER -> 0f
                }
                drawCircle(
                    color = AppcCoral.copy(alpha = 0.2f),
                    radius = radiusPx,
                    center = center
                )
                drawCircle(
                    color = AppcCoral,
                    radius = radiusPx,
                    center = center,
                    style = Stroke(width = 3f)
                )
            }

            // Repère / Pin de l'entrée
            val pinPath = Path().apply {
                moveTo(center.x, center.y)
                lineTo(center.x - 14f, center.y - 32f)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        center.x - 14f,
                        center.y - 46f,
                        center.x + 14f,
                        center.y - 18f
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(center.x, center.y)
                close()
            }
            drawPath(pinPath, color = AppcCoral)
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = Offset(center.x, center.y - 32f)
            )
        }

        Text(
            text = "GPS: ${String.format("%.4f", address.latitude)}, ${String.format("%.4f", address.longitude)}",
            fontSize = 11.sp,
            color = Color(0xFF555E6D),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(topStart = 6.dp))
        )
    }
}
