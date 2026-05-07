package com.propdf.editor.ui.tools

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.repository.PdfOperationsRepository
import com.propdf.editor.domain.usecase.*
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val mergePdfsUseCase: MergePdfsUseCase,
    private val splitPdfUseCase: SplitPdfUseCase,
    private val compressPdfUseCase: CompressPdfUseCase,
    private val extractTextUseCase: ExtractTextUseCase,
    private val operationsRepository: PdfOperationsRepository
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _result = MutableStateFlow<Result<File>?>(null)
    val result: StateFlow<Result<File>?> = _result.asStateFlow()

    private val _extractedText = MutableStateFlow<String?>(null)
    val extractedText: StateFlow<String?> = _extractedText.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun mergePdfs(files: List<Uri>, outputFile: File) {
        viewModelScope.launch {
            _isProcessing.value = true
            _progress.value = 0
            try {
                val result = mergePdfsUseCase(files, outputFile)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun splitPdf(file: Uri, pageRanges: List<IntRange>, outputDir: File) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = splitPdfUseCase(file, pageRanges, outputDir)
                result.onSuccess { files ->
                    _result.value = Result.success(files.firstOrNull() ?: File(""))
                }.onFailure { e ->
                    _error.value = e.message
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun compressPdf(file: Uri, outputFile: File, quality: Int = 5) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = compressPdfUseCase(file, outputFile, quality)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun extractText(file: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = extractTextUseCase(file)
                result.onSuccess { text ->
                    _extractedText.value = text
                }.onFailure { e ->
                    _error.value = e.message
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun rotatePages(file: Uri, outputFile: File, rotations: Map<Int, Int>) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.rotatePages(file, outputFile, rotations)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun deletePages(file: Uri, outputFile: File, pagesToDelete: List<Int>) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.deletePages(file, outputFile, pagesToDelete)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun addWatermark(file: Uri, outputFile: File, text: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.addWatermark(file, outputFile, text)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun encryptPdf(file: Uri, outputFile: File, password: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.encryptPdf(file, outputFile, password)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun imagesToPdf(images: List<Uri>, outputFile: File) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.imagesToPdf(images, outputFile)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun pdfToImages(file: Uri, outputDir: File) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val result = operationsRepository.pdfToImages(file, outputDir)
                result.onSuccess { files ->
                    _result.value = Result.success(files.firstOrNull() ?: File(""))
                }.onFailure { e ->
                    _error.value = e.message
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun clearResult() {
        _result.value = null
        _extractedText.value = null
        _error.value = null
    }
}
