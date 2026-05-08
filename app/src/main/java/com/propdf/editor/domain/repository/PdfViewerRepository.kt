package com.propdf.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.PdfDocument

interface PdfViewerRepository {
    fun openDocument(uri: Uri): Result<PdfDocument>
    fun renderPage(pageIndex: Int, width: Int, height: Int): Bitmap?
    fun getPageCount(): Int
    fun closeDocument()
    fun pauseRendering()
    fun resumeRendering()
}
