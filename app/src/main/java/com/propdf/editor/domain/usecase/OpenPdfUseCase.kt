package com.propdf.editor.domain.usecase

import android.net.Uri
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.repository.PdfViewerRepository
import javax.inject.Inject

class OpenPdfUseCase @Inject constructor(
    private val repository: PdfViewerRepository
) {
    operator fun invoke(uri: Uri): Result<PdfDocument> = repository.openDocument(uri)
}
