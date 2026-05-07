package com.propdf.editor.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.propdf.editor.domain.model.PdfDocumentInfo
import com.propdf.editor.domain.model.SearchResult
import com.propdf.editor.domain.repository.PdfViewerRepository
import com.propdf.editor.utils.DeviceCapabilities
import com.propdf.editor.utils.FileUtils
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfiumEngine @Inject constructor(
    private val context: Context
) : PdfViewerRepository {

    private var pdfiumCore: PdfiumCore? = null
    private var pdfDocument: PdfDocument? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private var currentFile: File? = null
    private val mutex = Mutex()
    private var pageCount: Int = 0

    override suspend fun openDocument(uri: Uri): Result<PdfDocumentInfo> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                closeDocumentInternal()

                val file = FileUtils.uriToTempFile(context, uri)
                    ?: return@withContext Result.failure(Exception("Cannot open file"))

                currentFile = file
                pdfiumCore = PdfiumCore(context)
                parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

                pdfDocument = pdfiumCore!!.newDocument(parcelFileDescriptor)
                pageCount = pdfiumCore!!.getPageCount(pdfDocument)

                val meta = pdfiumCore!!.getDocumentMeta(pdfDocument)

                Result.success(
                    PdfDocumentInfo(
                        pageCount = pageCount,
                        title = meta.title,
                        author = meta.author,
                        creationDate = meta.creationDate
                    )
                )
            } catch (e: Exception) {
                closeDocumentInternal()
                Result.failure(e)
            }
        }
    }

    override suspend fun renderPage(pageNumber: Int, width: Int, height: Int): Result<Bitmap> =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    val doc = pdfDocument ?: return@withContext Result.failure(
                        IllegalStateException("Document not opened")
                    )

                    if (pageNumber < 0 || pageNumber >= pageCount) {
                        return@withContext Result.failure(
                            IllegalArgumentException("Invalid page number: $pageNumber")
                        )
                    }

                    val quality = DeviceCapabilities.getOptimalPdfRenderQuality()
                    val scaleFactor = quality / 72f // PDF default is 72 DPI

                    pdfiumCore!!.openPage(doc, pageNumber)
                    val pageWidth = pdfiumCore!!.getPageWidthPoint(doc, pageNumber)
                    val pageHeight = pdfiumCore!!.getPageHeightPoint(doc, pageNumber)

                    val renderWidth = (pageWidth * scaleFactor).toInt().coerceAtMost(width)
                    val renderHeight = (pageHeight * scaleFactor).toInt().coerceAtMost(height)

                    // Use RGB_565 on low-end devices to save memory
                    val config = if (DeviceCapabilities.isLowRamDevice) {
                        Bitmap.Config.RGB_565
                    } else {
                        Bitmap.Config.ARGB_8888
                    }

                    val bitmap = Bitmap.createBitmap(renderWidth, renderHeight, config)
                    pdfiumCore!!.renderPageBitmap(
                        doc, bitmap, pageNumber,
                        0, 0, renderWidth, renderHeight
                    )

                    Result.success(bitmap)
                } catch (e: OutOfMemoryError) {
                    System.gc()
                    Result.failure(Exception("Not enough memory to render page"))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }

    override suspend fun getPageCount(): Int = pageCount

    override suspend fun searchInDocument(query: String): Result<List<SearchResult>> =
        withContext(Dispatchers.IO) {
            // Pdfium text search implementation
            // This is a simplified version - full implementation would use pdfium text extraction
            Result.success(emptyList())
        }

    override suspend fun closeDocument() {
        mutex.withLock {
            closeDocumentInternal()
        }
    }

    private fun closeDocumentInternal() {
        try {
            pdfDocument?.let { pdfiumCore?.closeDocument(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pdfDocument = null
        pdfiumCore = null

        try {
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        parcelFileDescriptor = null

        currentFile?.delete()
        currentFile = null
        pageCount = 0
    }

    fun getPageSize(pageNumber: Int): Pair<Int, Int>? {
        val doc = pdfDocument ?: return null
        return try {
            val width = pdfiumCore?.getPageWidthPoint(doc, pageNumber) ?: 0
            val height = pdfiumCore?.getPageHeightPoint(doc, pageNumber) ?: 0
            Pair(width, height)
        } catch (e: Exception) {
            null
        }
    }
}
