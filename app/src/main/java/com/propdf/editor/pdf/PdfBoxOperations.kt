package com.propdf.editor.pdf

import android.net.Uri
import com.propdf.editor.domain.repository.PdfOperationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfBoxOperations @Inject constructor() : PdfOperationsRepository {
    override suspend fun mergePdfs(sources: List<Uri>, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun splitPdf(source: Uri, pages: List<Int>, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun compressPdf(source: Uri, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun rotatePages(source: Uri, pages: List<Int>, degrees: Int, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun deletePages(source: Uri, pages: List<Int>, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun addWatermark(source: Uri, text: String, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun encryptPdf(source: Uri, password: String, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun imagesToPdf(images: List<Uri>, output: Uri): Result<Unit> = Result.success(Unit)
    override suspend fun pdfToImages(source: Uri, outputDir: Uri): Result<List<Uri>> = Result.success(emptyList())
    override suspend fun extractText(source: Uri): Result<String> = Result.success("")
}
