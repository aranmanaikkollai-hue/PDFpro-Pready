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
    private val getRecentFilesUseCase: GetRecentFilesUseCase,
    private val addRecentFileUseCase: AddRecentFileUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val recentFiles: StateFlow<List<PdfFile>> = _recentFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadRecent() }

    private fun loadRecent() {
        viewModelScope.launch {
            _isLoading.value = true
            getRecentFilesUseCase().collect { _recentFiles.value = it }
            _isLoading.value = false
        }
    }

    fun addRecentFile(file: PdfFile) {
        viewModelScope.launch {
            addRecentFileUseCase(file)
            loadRecent()
        }
    }

    fun toggleFavorite(uri: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(uri)
            loadRecent()
        }
    }
}
