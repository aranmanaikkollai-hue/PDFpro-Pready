package com.propdf.editor.ui.tools

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.repository.PdfOperationsRepository
import com.propdf.editor.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val mergePdfsUseCase: MergePdfsUseCase,
    private val splitPdfUseCase: SplitPdfUseCase,
    private val compressPdfUseCase: CompressPdfUseCase,
    private val extractTextUseCase: ExtractTextUseCase,
    private val pdfOperationsRepository: PdfOperationsRepository
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result

    private val _extractedText = MutableStateFlow<String?>(null)
    val extractedText: StateFlow<String?> = _extractedText

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearResult() {
        _result.value = null
        _error.value = null
        _extractedText.value = null
    }

    fun mergePdfs(sources: List<Uri>, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            mergePdfsUseCase(sources, output)
                .onSuccess { _result.value = "PDFs merged successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun splitPdf(source: Uri, pages: List<Int>, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            splitPdfUseCase(source, pages, output)
                .onSuccess { _result.value = "PDF split successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun compressPdf(source: Uri, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            compressPdfUseCase(source, output)
                .onSuccess { _result.value = "PDF compressed successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun rotatePages(source: Uri, pages: List<Int>, degrees: Int, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.rotatePages(source, pages, degrees, output)
                .onSuccess { _result.value = "Pages rotated successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun deletePages(source: Uri, pages: List<Int>, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.deletePages(source, pages, output)
                .onSuccess { _result.value = "Pages deleted successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun addWatermark(source: Uri, text: String, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.addWatermark(source, text, output)
                .onSuccess { _result.value = "Watermark added successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun encryptPdf(source: Uri, password: String, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.encryptPdf(source, password, output)
                .onSuccess { _result.value = "PDF encrypted successfully" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun imagesToPdf(images: List<Uri>, output: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.imagesToPdf(images, output)
                .onSuccess { _result.value = "Images converted to PDF" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun pdfToImages(source: Uri, outputDir: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            pdfOperationsRepository.pdfToImages(source, outputDir)
                .onSuccess { uris -> _result.value = "Saved ${uris.size} images" }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }

    fun extractText(source: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearResult()
            extractTextUseCase(source)
                .onSuccess { _extractedText.value = it }
                .onFailure { _error.value = it.message }
            _isProcessing.value = false
        }
    }
}
