package fr.parentsapariscentre.badgecreche.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QrCodeReadingTest {

    private val payload = "APPC-DEMO-BADGE-0001"

    private fun qr(text: String = payload, size: Int = 300): BitMatrix =
        QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, mapOf(EncodeHintType.MARGIN to 2))

    /** QR code posé sur une image plus grande (fond clair), comme une photo ou une capture d'écran. */
    private fun pixels(matrix: BitMatrix, width: Int, height: Int, left: Int, top: Int,
                       dark: Int = 0xFF000000.toInt(), light: Int = 0xFFFFFFFF.toInt()): IntArray {
        val image = IntArray(width * height) { light }
        for (y in 0 until matrix.height) for (x in 0 until matrix.width) {
            if (matrix[x, y]) image[(top + y) * width + left + x] = dark
        }
        return image
    }

    @Test
    fun readsAQrCodeInAPhoto() {
        val image = pixels(qr(), 1200, 1600, 450, 700)
        assertEquals(payload, QrCodeReading.fromPixels(1200, 1600, image))
    }

    @Test
    fun readsALightQrCodeOnADarkBackground() {
        val image = pixels(qr(), 800, 800, 250, 250, dark = 0xFFFFFFFF.toInt(), light = 0xFF101010.toInt())
        assertEquals(payload, QrCodeReading.fromPixels(800, 800, image))
    }

    @Test
    fun returnsNothingWithoutQrCode() {
        assertNull(QrCodeReading.fromPixels(400, 400, IntArray(400 * 400) { 0xFFFFFFFF.toInt() }))
    }

    @Test
    fun readsTheCentreOfACameraFrameInAnyOrientation() {
        val width = 1280
        val height = 720
        val matrix = qr(size = 400)
        for (rotated in listOf(false, true)) {
            val luminance = ByteArray(width * height) { 0xE0.toByte() }
            for (y in 0 until matrix.height) for (x in 0 until matrix.width) {
                val dark = if (rotated) matrix[y, matrix.height - 1 - x] else matrix[x, y]
                if (dark) luminance[(160 + y) * width + 440 + x] = 0x10
            }
            assertEquals(payload, QrCodeReading.fromLuminance(width, height, luminance))
        }
    }

    @Test
    fun keepsTheExactContentOfTheBadge() {
        val text = "2024ABC0000001;Crèche « Les Lucioles » & co"
        val image = pixels(qr(text), 900, 900, 300, 300)
        assertEquals(text, QrCodeReading.fromPixels(900, 900, image))
    }
}
