package com.propdf.editor.domain.usecase

import com.propdf.editor.domain.model.ScanPage
import javax.inject.Inject

class SaveScanUseCase @Inject constructor() {
    operator fun invoke(pages: List<ScanPage>): Result<String> {
        return try {
            // TODO: Implement PDF generation from scanned pages
            Result.success("saved")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
