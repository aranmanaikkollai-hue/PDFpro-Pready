package com.propdf.editor.ui.viewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.repository.PdfViewerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val pdfViewerRepository: PdfViewerRepository
) : ViewModel() {

    private val _document = MutableStateFlow<PdfDocument?>(null)
    val document: StateFlow<PdfDocument?> = _document

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun openDocument(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = pdfViewerRepository.openDocument(uri)
            result.fold(
                onSuccess = { doc ->
                    _document.value = doc
                    _pageCount.value = doc.pageCount
                    _currentPage.value = 0
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
            _isLoading.value = false
        }
    }

    fun goToPage(page: Int) {
        if (page in 0 until _pageCount.value) {
            _currentPage.value = page
        }
    }

    fun searchInDocument(query: String) {
        viewModelScope.launch {
            pdfViewerRepository.searchInDocument(query)
        }
    }

    fun closeDocument() {
        pdfViewerRepository.closeDocument()
    }

    override fun onCleared() {
        super.onCleared()
        closeDocument()
    }
}
