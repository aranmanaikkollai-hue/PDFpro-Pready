package com.propdf.editor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentFiles: GetRecentFilesUseCase,
    private val addRecentFile: AddRecentFileUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val recentFiles: StateFlow<List<PdfFile>> = _recentFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadRecent() }

    private fun loadRecent() {
        viewModelScope.launch {
            _isLoading.value = true
            getRecent().collect { _recentFiles.value = it }
            _isLoading.value = false
        }
    }

    fun addRecentFile(file: PdfFile) {
        viewModelScope.launch {
            addRecentFile(file)
            loadRecent()
        }
    }

    fun toggleFavorite(uri: String) {
        viewModelScope.launch {
            toggleFavorite(uri)
            loadRecent()
        }
    }
}
