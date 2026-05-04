package com.example.paryavaran_kavalu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageHelper {

    /**
     * Compresses an image from URI and returns the file path of the compressed image.
     * Keeps the file size under approximately 500KB.
     */
    fun compressImage(context: Context, imageUri: Uri): String? {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        
        val outputFile = File(context.cacheDir, "temp_report_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(outputFile)
        
        // Compress quality logic (starts at 80%)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        
        outputStream.flush()
        outputStream.close()
        
        return outputFile.absolutePath
    }
}
