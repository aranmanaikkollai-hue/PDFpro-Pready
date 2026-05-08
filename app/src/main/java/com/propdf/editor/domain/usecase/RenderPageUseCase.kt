package com.propdf.editor.domain.usecase

import android.graphics.Bitmap
import com.propdf.editor.domain.repository.PdfViewerRepository
import javax.inject.Inject

class RenderPageUseCase @Inject constructor(
    private val repository: PdfViewerRepository
) {
    operator fun invoke(pageIndex: Int, width: Int, height: Int): Bitmap? {
        return repository.renderPage(pageIndex, width, height)
    }
}
