package com.propdf.editor.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.data.pdfium.PdfiumEngine
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.model.PdfPage
import com.propdf.editor.domain.repository.PdfViewerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfViewerRepositoryImpl @Inject constructor(
    private val pdfiumEngine: PdfiumEngine
) : PdfViewerRepository {

    override fun openDocument(uri: Uri): Result<PdfDocument> = pdfiumEngine.openDocument(uri)
    override fun renderPage(pageIndex: Int, width: Int, height: Int): Bitmap? =
        pdfiumEngine.renderPage(pageIndex, width, height)
    override fun getPageCount(): Int = pdfiumEngine.getPageCount()
    override fun closeDocument() = pdfiumEngine.closeDocument()
    override fun pauseRendering() = pdfiumEngine.pauseRendering()
    override fun resumeRendering() = pdfiumEngine.resumeRendering()
    override fun searchInDocument(query: String): List<PdfPage> = emptyList()
}
