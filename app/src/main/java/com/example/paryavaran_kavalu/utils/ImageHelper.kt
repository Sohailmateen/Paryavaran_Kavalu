package com.example.paryavaran_kavalu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageHelper {

    fun getTempImageUri(context: Context): Uri {
        val tempFile = File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    /**
     * Resizes and compresses an image to keep it under 500KB.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Step 1: Resize if too large (Max width/height 1280px)
            val maxWidth = 1280
            val maxHeight = 1280
            if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val scale = Math.min(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            }

            val fileName = "report_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            
            var quality = 90
            var fileSize: Long
            
            // Step 2: Compress iteratively to target < 500KB
            do {
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
                fileSize = file.length()
                quality -= 10
            } while (fileSize > 500 * 1024 && quality > 10)

            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
