package com.brijesh.smartscanner.feature.generator

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QrCodeImageActions {

    fun saveToGallery(context: Context, bitmap: Bitmap) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "QR_${System.currentTimeMillis()}.png"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/SmartScanner"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: throw IllegalStateException("Unable to create image")

        try {
            resolver.openOutputStream(uri)?.use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                    throw IllegalStateException("Unable to encode image")
                }
            } ?: throw IllegalStateException("Unable to open output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val updateValues = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                resolver.update(uri, updateValues, null, null)
            }

            Toast.makeText(context, "QR code saved to Pictures", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            Toast.makeText(context, "Failed to save QR code", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareImage(context: Context, bitmap: Bitmap) {
        val imagesDir = File(context.cacheDir, "shared_qr")
        imagesDir.mkdirs()

        val imageFile = File(
            imagesDir,
            "qr_${System.currentTimeMillis()}.png"
        )

        FileOutputStream(imageFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share QR code")
        )
    }
}