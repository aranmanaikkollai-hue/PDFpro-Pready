package com.propdf.editor.ui.viewer

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.model.PdfBookmark
import com.propdf.editor.domain.repository.*
import com.propdf.editor.utils.DeviceCapabilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val viewerRepository: PdfViewerRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val annotationRepository: AnnotationRepository,
    private val ocrRepository: OcrRepository
) : ViewModel() {

    private val _pdfUri = MutableStateFlow<Uri?>(null)
    val pdfUri: StateFlow<Uri?> = _pdfUri.asStateFlow()

    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<PdfBookmark>>(emptyList())
    val bookmarks: StateFlow<List<PdfBookmark>> = _bookmarks.asStateFlow()

    private val _searchResults = MutableStateFlow<List<com.propdf.editor.domain.model.SearchResult>>(emptyList())
    val searchResults: StateFlow<List<com.propdf.editor.domain.model.SearchResult>> = _searchResults.asStateFlow()

    private val _isAnnotationMode = MutableStateFlow(false)
    val isAnnotationMode: StateFlow<Boolean> = _isAnnotationMode.asStateFlow()

    private val _ocrText = MutableStateFlow<String?>(null)
    val ocrText: StateFlow<String?> = _ocrText.asStateFlow()

    private val pageCache = mutableMapOf<Int, Bitmap>()
    private val maxCacheSize = DeviceCapabilities.getOptimalBitmapPoolSize()

    private val _pageBitmap = MutableStateFlow<Bitmap?>(null)
    val pageBitmap: StateFlow<Bitmap?> = _pageBitmap.asStateFlow()

    fun openDocument(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pdfUri.value = uri
                val result = viewerRepository.openDocument(uri)
                result.onSuccess { info ->
                    _pageCount.value = info.pageCount
                    _currentPage.value = 0
                    loadPage(0)
                    loadBookmarks(uri.toString())
                }.onFailure { e ->
                    _error.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPage(pageNumber: Int) {
        viewModelScope.launch {
            if (pageNumber < 0 || pageNumber >= _pageCount.value) return@launch

            // Check cache first
            pageCache[pageNumber]?.let {
                _pageBitmap.value = it
                _currentPage.value = pageNumber
                return@launch
            }

            // Evict old cache entries if needed
            while (pageCache.size >= maxCacheSize) {
                pageCache.keys.firstOrNull()?.let { key ->
                    pageCache.remove(key)?.recycle()
                }
            }

            val result = viewerRepository.renderPage(
                pageNumber,
                width = 1080,
                height = 1920
            )
            result.onSuccess { bitmap ->
                pageCache[pageNumber] = bitmap
                _pageBitmap.value = bitmap
                _currentPage.value = pageNumber
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    fun nextPage() {
        loadPage(_currentPage.value + 1)
    }

    fun previousPage() {
        loadPage(_currentPage.value - 1)
    }

    fun jumpToPage(pageNumber: Int) {
        loadPage(pageNumber)
    }

    fun toggleAnnotationMode() {
        _isAnnotationMode.value = !_isAnnotationMode.value
    }

    fun addBookmark(title: String) {
        viewModelScope.launch {
            val uri = _pdfUri.value?.toString() ?: return@launch
            val bookmark = PdfBookmark(
                pdfUri = uri,
                pageNumber = _currentPage.value,
                title = title
            )
            bookmarkRepository.addBookmark(bookmark)
            loadBookmarks(uri)
        }
    }

    private fun loadBookmarks(pdfUri: String) {
        viewModelScope.launch {
            bookmarkRepository.getBookmarks(pdfUri).collect { list ->
                _bookmarks.value = list
            }
        }
    }

    fun searchInDocument(query: String) {
        viewModelScope.launch {
            val result = viewerRepository.searchInDocument(query)
            result.onSuccess { results ->
                _searchResults.value = results
            }
        }
    }

    fun runOcrOnCurrentPage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = ocrRepository.recognizeText(bitmap)
                result.onSuccess { ocrResult ->
                    _ocrText.value = ocrResult.text
                }.onFailure { e ->
                    _error.value = e.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearOcrResult() {
        _ocrText.value = null
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        pageCache.values.forEach { it.recycle() }
        pageCache.clear()
        viewModelScope.launch {
            viewerRepository.closeDocument()
        }
    }
}
