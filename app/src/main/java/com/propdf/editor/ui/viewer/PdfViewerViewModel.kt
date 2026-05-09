package com.propdf.editor.ui.viewer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel @Inject constructor() : ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun goToPage(page: Int) { _currentPage.value = page }
    fun setPageCount(count: Int) { _pageCount.value = count }
}
