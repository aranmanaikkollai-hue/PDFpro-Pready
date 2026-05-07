package com.propdf.editor.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.usecase.*
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getRecentFilesUseCase: GetRecentFilesUseCase,
    private val getFavoriteFilesUseCase: GetFavoriteFilesUseCase,
    private val addRecentFileUseCase: AddRecentFileUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val searchFilesUseCase: SearchFilesUseCase,
    private val getPerformanceProfileUseCase: GetPerformanceProfileUseCase
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val recentFiles: StateFlow<List<PdfFile>> = _recentFiles.asStateFlow()

    private val _favoriteFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val favoriteFiles: StateFlow<List<PdfFile>> = _favoriteFiles.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PdfFile>>(emptyList())
    val searchResults: StateFlow<List<PdfFile>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _performanceProfile = MutableStateFlow<com.propdf.editor.domain.model.DevicePerformanceProfile?>(null)
    val performanceProfile: StateFlow<com.propdf.editor.domain.model.DevicePerformanceProfile?> = _performanceProfile.asStateFlow()

    init {
        loadRecentFiles()
        loadFavoriteFiles()
        loadPerformanceProfile()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getRecentFilesUseCase(20).collect { files ->
                    _recentFiles.value = files
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFavoriteFiles() {
        viewModelScope.launch {
            getFavoriteFilesUseCase().collect { files ->
                _favoriteFiles.value = files
            }
        }
    }

    private fun loadPerformanceProfile() {
        viewModelScope.launch {
            _performanceProfile.value = getPerformanceProfileUseCase()
        }
    }

    fun addRecentFile(uri: Uri) {
        viewModelScope.launch {
            try {
                addRecentFileUseCase(uri)
                loadRecentFiles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite(pdfFile: PdfFile) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(pdfFile)
                loadRecentFiles()
                loadFavoriteFiles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun searchFiles(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }
            searchFilesUseCase(query).collect { files ->
                _searchResults.value = files
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
