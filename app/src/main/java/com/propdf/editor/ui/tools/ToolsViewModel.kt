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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow<String?>(null)
    val success: StateFlow<String?> = _success

    fun mergePdfs(sources: List<Uri>, output: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            mergePdfsUseCase(sources, output)
                .onSuccess { _success.value = "Merged" }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun splitPdf(source: Uri, pages: List<Int>, output: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            splitPdfUseCase(source, pages, output)
                .onSuccess { _success.value = "Split" }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun compressPdf(source: Uri, output: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            compressPdfUseCase(source, output)
                .onSuccess { _success.value = "Compressed" }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun extractText(source: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            extractTextUseCase(source)
                .onSuccess { _success.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
