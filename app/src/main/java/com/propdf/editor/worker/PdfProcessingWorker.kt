package com.propdf.editor.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.propdf.editor.domain.repository.PdfOperationsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PdfProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val pdfOperations: PdfOperationsRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_OPERATION = "operation"
        const val KEY_SOURCE = "source"
        const val KEY_OUTPUT = "output"
        const val KEY_PAGES = "pages"
        const val KEY_DEGREES = "degrees"
        const val KEY_TEXT = "text"
        const val KEY_PASSWORD = "password"
    }

    override suspend fun doWork(): Result {
        val operation = inputData.getString(KEY_OPERATION) ?: return Result.failure()
        val source = inputData.getString(KEY_SOURCE)?.let { Uri.parse(it) } ?: return Result.failure()
        val output = inputData.getString(KEY_OUTPUT)?.let { Uri.parse(it) } ?: return Result.failure()

        val res = when (operation) {
            "merge" -> {
                val sources = inputData.getStringArray(KEY_SOURCE)?.map { Uri.parse(it) } ?: emptyList()
                pdfOperations.mergePdfs(sources, output)
            }
            "split" -> {
                val pages = inputData.getIntArray(KEY_PAGES)?.toList() ?: emptyList()
                pdfOperations.splitPdf(source, pages, output)
            }
            "compress" -> pdfOperations.compressPdf(source, output)
            "rotate" -> {
                val pages = inputData.getIntArray(KEY_PAGES)?.toList() ?: emptyList()
                val degrees = inputData.getInt(KEY_DEGREES, 90)
                pdfOperations.rotatePages(source, pages, degrees, output)
            }
            "delete" -> {
                val pages = inputData.getIntArray(KEY_PAGES)?.toList() ?: emptyList()
                pdfOperations.deletePages(source, pages, output)
            }
            "watermark" -> {
                val text = inputData.getString(KEY_TEXT) ?: ""
                pdfOperations.addWatermark(source, text, output)
            }
            "encrypt" -> {
                val password = inputData.getString(KEY_PASSWORD) ?: ""
                pdfOperations.encryptPdf(source, password, output)
            }
            else -> kotlin.Result.failure(IllegalArgumentException("Unknown operation"))
        }

        return if (res.isSuccess) Result.success() else Result.failure()
    }
}
