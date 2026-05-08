package com.propdf.editor.domain.usecase

import android.net.Uri
import com.propdf.editor.domain.repository.PdfOperationsRepository
import javax.inject.Inject

class MergePdfsUseCase @Inject constructor(
    private val repository: PdfOperationsRepository
) {
    suspend operator fun invoke(sources: List<Uri>, output: Uri) = repository.mergePdfs(sources, output)
}

class SplitPdfUseCase @Inject constructor(
    private val repository: PdfOperationsRepository
) {
    suspend operator fun invoke(source: Uri, pages: List<Int>, output: Uri) = repository.splitPdf(source, pages, output)
}

class CompressPdfUseCase @Inject constructor(
    private val repository: PdfOperationsRepository
) {
    suspend operator fun invoke(source: Uri, output: Uri) = repository.compressPdf(source, output)
}

class ExtractTextUseCase @Inject constructor(
    private val repository: PdfOperationsRepository
) {
    suspend operator fun invoke(source: Uri) = repository.extractText(source)
}
