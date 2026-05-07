package com.propdf.editor.domain.usecase

import android.content.Context
import android.net.Uri
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.repository.*
import com.propdf.editor.utils.FileUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRecentFilesUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(limit: Int = 20): Flow<List<PdfFile>> {
        return pdfRepository.getRecentFiles(limit)
    }
}

class GetFavoriteFilesUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(): Flow<List<PdfFile>> {
        return pdfRepository.getFavoriteFiles()
    }
}

class AddRecentFileUseCase @Inject constructor(
    private val pdfRepository: PdfRepository,
    private val context: Context
) {
    suspend operator fun invoke(uri: Uri) {
        val name = FileUtils.getFileName(context, uri) ?: "Unknown"
        val size = FileUtils.getFileSize(context, uri)
        val pdfFile = PdfFile(
            name = name,
            uri = uri.toString(),
            size = size
        )
        pdfRepository.addRecentFile(pdfFile)
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(pdfFile: PdfFile) {
        pdfRepository.toggleFavorite(pdfFile)
    }
}

class SearchFilesUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(query: String): Flow<List<PdfFile>> {
        return pdfRepository.searchFiles(query)
    }
}

class MergePdfsUseCase @Inject constructor(
    private val operationsRepository: PdfOperationsRepository
) {
    suspend operator fun invoke(files: List<Uri>, outputFile: java.io.File): Result<java.io.File> {
        return operationsRepository.mergePdfs(files, outputFile)
    }
}

class SplitPdfUseCase @Inject constructor(
    private val operationsRepository: PdfOperationsRepository
) {
    suspend operator fun invoke(
        file: Uri,
        pageRanges: List<IntRange>,
        outputDir: java.io.File
    ): Result<List<java.io.File>> {
        return operationsRepository.splitPdf(file, pageRanges, outputDir)
    }
}

class CompressPdfUseCase @Inject constructor(
    private val operationsRepository: PdfOperationsRepository
) {
    suspend operator fun invoke(
        file: Uri,
        outputFile: java.io.File,
        quality: Int = 5
    ): Result<java.io.File> {
        return operationsRepository.compressPdf(file, outputFile, quality)
    }
}

class ExtractTextUseCase @Inject constructor(
    private val operationsRepository: PdfOperationsRepository
) {
    suspend operator fun invoke(file: Uri): Result<String> {
        return operationsRepository.extractText(file)
    }
}

class GetPerformanceProfileUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() = settingsRepository.getPerformanceProfile()
}
