package com.propdf.editor.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun createTempPdf(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(context.cacheDir, "PDF_${timeStamp}.pdf")
    }

    fun createOutputUri(context: Context, fileName: String): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/ProPDF")
            }
            context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                ?: throw IllegalStateException("Cannot create MediaStore entry")
        } else {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ProPDF/$fileName")
            file.parentFile?.mkdirs()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
    }

    fun getFileName(context: Context, uri: Uri): String {
        return when (uri.scheme) {
            "content" -> {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else "document.pdf"
                } ?: "document.pdf"
            }
            "file" -> File(uri.path!!).name
            else -> "document.pdf"
        }
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
    }
}
