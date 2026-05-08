package com.propdf.editor.pdf

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore

class PdfiumEngine(private val context: Context) {

    private val pdfiumCore = PdfiumCore(context)

    fun getPageCount(uri: Uri): Int {
        val fd = context.contentResolver.openFileDescriptor(uri, "r") ?: return 0
        val doc = pdfiumCore.newDocument(fd)
        val count = pdfiumCore.getPageCount(doc)
        pdfiumCore.closeDocument(doc)
        fd.close()
        return count
    }

    fun renderPage(uri: Uri, pageIndex: Int, width: Int, height: Int): Bitmap {
        val fd = context.contentResolver.openFileDescriptor(uri, "r")!!
        val doc = pdfiumCore.newDocument(fd)
        pdfiumCore.openPage(doc, pageIndex)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        pdfiumCore.renderPageBitmap(doc, bitmap, pageIndex, 0, 0, width, height)
        pdfiumCore.closeDocument(doc)
        fd.close()
        return bitmap
    }

    fun getDocumentMeta(uri: Uri): Map<String, String> {
        val fd = context.contentResolver.openFileDescriptor(uri, "r") ?: return emptyMap()
        val doc = pdfiumCore.newDocument(fd)
        val meta = pdfiumCore.getDocumentMeta(doc)
        val result = mapOf(
            "title" to (meta.title ?: ""),
            "author" to (meta.author ?: ""),
            "subject" to (meta.subject ?: ""),
            "keywords" to (meta.keywords ?: ""),
            "creator" to (meta.creator ?: ""),
            "producer" to (meta.producer ?: ""),
            "creationDate" to (meta.creationDate ?: ""),
            "modDate" to (meta.modDate ?: "")
        )
        pdfiumCore.closeDocument(doc)
        fd.close()
        return result
    }
}
