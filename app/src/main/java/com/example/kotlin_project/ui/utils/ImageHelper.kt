package com.example.kotlin_project.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageHelper {

    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {

            val fileName = "dish_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)


            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(context: Context, imagePath: String?) {
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}