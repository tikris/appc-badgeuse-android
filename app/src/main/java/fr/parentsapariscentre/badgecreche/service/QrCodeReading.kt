package fr.parentsapariscentre.badgecreche.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader

/**
 * Lecture d'un QR code avec ZXing, entièrement sur le téléphone : aucune donnée n'est envoyée
 * (contrairement à ML Kit, qui transmet des diagnostics à Google). Kotlin pur, testé sur la JVM.
 */
object QrCodeReading {

    /** Photo, capture d'écran ou page de PDF : pixels ARGB, recherche approfondie. */
    fun fromPixels(width: Int, height: Int, pixels: IntArray): String? =
        decode(RGBLuminanceSource(width, height, pixels), tryHarder = true)

    /**
     * Image de la caméra : plan de luminance (Y), lu au centre, là où se trouve le viseur.
     * Recherche rapide pour suivre le flux vidéo ; un QR code se lit dans tous les sens.
     */
    fun fromLuminance(width: Int, height: Int, luminance: ByteArray): String? {
        val side = (minOf(width, height) * 0.8).toInt()
        val source = PlanarYUVLuminanceSource(
            luminance, width, height, (width - side) / 2, (height - side) / 2, side, side, false
        )
        return decode(source, tryHarder = false)
    }

    private fun decode(source: LuminanceSource, tryHarder: Boolean): String? {
        val hints = mutableMapOf<DecodeHintType, Any>(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE))
        if (tryHarder) hints[DecodeHintType.TRY_HARDER] = true
        // QR code clair sur fond sombre (thème sombre, capture d'écran) : essayé aussi en négatif.
        for (candidate in listOf(source, source.invert())) {
            for (bitmap in listOf(BinaryBitmap(HybridBinarizer(candidate)), BinaryBitmap(GlobalHistogramBinarizer(candidate)))) {
                try {
                    val text = QRCodeReader().decode(bitmap, hints).text
                    if (!text.isNullOrBlank()) return text
                } catch (e: ReaderException) {
                    // Pas de QR code lisible avec cette méthode : on essaie la suivante.
                }
            }
        }
        return null
    }
}
