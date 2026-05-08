package com.propdf.editor.data.pdfium

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.propdf.editor.domain.model.PdfDocument
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfiumEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var pdfiumCore: PdfiumCore? = null
    private var currentDocument: PdfDocument? = null
    private var currentUri: Uri? = null

    fun openDocument(uri: Uri): Result<PdfDocument> {
        return try {
            closeDocument()
            pdfiumCore = PdfiumCore(context)
            val fd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return Result.failure(IllegalStateException("Cannot open file descriptor"))
            currentDocument = pdfiumCore!!.newDocument(fd)
            currentUri = uri
            val count = pdfiumCore!!.getPageCount(currentDocument!!)
            Result.success(
                PdfDocument(
                    uri = uri,
                    name = uri.lastPathSegment ?: "document.pdf",
                    pageCount = count
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun renderPage(pageIndex: Int, width: Int, height: Int): Bitmap? {
        val doc = currentDocument ?: return null
        val core = pdfiumCore ?: return null
        return try {
            core.openPage(doc, pageIndex)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            core.renderPageBitmap(doc, bitmap, pageIndex, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPageCount(): Int {
        val doc = currentDocument ?: return 0
        val core = pdfiumCore ?: return 0
        return try {
            core.getPageCount(doc)
        } catch (e: Exception) {
            0
        }
    }

    fun closeDocument() {
        try {
            currentDocument?.let { pdfiumCore?.closeDocument(it) }
        } catch (_: Exception) { }
        currentDocument = null
        currentUri = null
    }

    fun pauseRendering() {
        // TODO: Implement if using custom rendering thread
    }

    fun resumeRendering() {
        // TODO: Implement if using custom rendering thread
    }
}
