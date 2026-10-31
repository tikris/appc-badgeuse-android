package fr.parentsapariscentre.badgecreche.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.math.max

object QRCodeDecoder {

    sealed class DecodeResult {
        data class Success(val payload: String) : DecodeResult()
        object NoQrCode : DecodeResult()
        data class Error(val message: String) : DecodeResult()
    }

    suspend fun decodeFromUri(context: Context, uri: Uri): DecodeResult = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: ""

        if (type == "application/pdf" || uri.toString().endsWith(".pdf", ignoreCase = true)) {
            return@withContext decodeFromPdf(context, uri)
        }

        try {
            val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                decodeDownsampledBitmap(inputStream, 2048)
            } ?: return@withContext DecodeResult.Error("Impossible d'ouvrir l'image sélectionnée.")

            decodeFromBitmap(bitmap)
        } catch (e: Exception) {
            DecodeResult.Error(e.localizedMessage ?: "Erreur lors de la lecture du fichier.")
        }
    }

    suspend fun decodeFromBitmap(bitmap: Bitmap): DecodeResult = withContext(Dispatchers.Default) {
        try {
            val scanner = BarcodeScanning.getClient()
            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = scanner.process(image).await()

            val qrCode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
            val rawValue = qrCode?.rawValue

            if (!rawValue.isNullOrBlank()) {
                DecodeResult.Success(rawValue)
            } else {
                DecodeResult.NoQrCode
            }
        } catch (e: Exception) {
            DecodeResult.Error(e.localizedMessage ?: "Erreur d'analyse de l'image.")
        }
    }

    private suspend fun decodeFromPdf(context: Context, uri: Uri): DecodeResult = withContext(Dispatchers.IO) {
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            pfd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return@withContext DecodeResult.Error("Impossible d'ouvrir le document PDF.")
            renderer = PdfRenderer(pfd)
            val pageCount = renderer.pageCount.coerceAtMost(5)

            for (i in 0 until pageCount) {
                val page = renderer.openPage(i)
                val width = page.width
                val height = page.height
                val scale = 2048f / max(width, height)
                val renderWidth = (width * scale).toInt()
                val renderHeight = (height * scale).toInt()

                val bitmap = Bitmap.createBitmap(renderWidth, renderHeight, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                when (val result = decodeFromBitmap(bitmap)) {
                    is DecodeResult.Success -> return@withContext result
                    else -> Unit
                }
            }
            DecodeResult.NoQrCode
        } catch (e: Exception) {
            DecodeResult.Error(e.localizedMessage ?: "Erreur de lecture du PDF.")
        } finally {
            renderer?.close()
            pfd?.close()
        }
    }

    private fun decodeDownsampledBitmap(inputStream: InputStream, maxDimension: Int): Bitmap? {
        val bytes = inputStream.readBytes()
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        val width = options.outWidth
        val height = options.outHeight
        var sampleSize = 1
        while (width / sampleSize > maxDimension || height / sampleSize > maxDimension) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)
    }
}
