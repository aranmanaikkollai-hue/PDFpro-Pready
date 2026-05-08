package com.propdf.editor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propdf.editor.domain.model.DevicePerformanceProfile
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentFilesUseCase: GetRecentFilesUseCase,
    private val getFavoriteFilesUseCase: GetFavoriteFilesUseCase,
    private val addRecentFileUseCase: AddRecentFileUseCase,
    private val searchFilesUseCase: SearchFilesUseCase,
    private val getPerformanceProfileUseCase: GetPerformanceProfileUseCase
) : ViewModel() {

    private val _recentFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val recentFiles: StateFlow<List<PdfFile>> = _recentFiles.asStateFlow()

    private val _favoriteFiles = MutableStateFlow<List<PdfFile>>(emptyList())
    val favoriteFiles: StateFlow<List<PdfFile>> = _favoriteFiles.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PdfFile>>(emptyList())
    val searchResults: StateFlow<List<PdfFile>> = _searchResults.asStateFlow()

    private val _performanceProfile = MutableStateFlow(DevicePerformanceProfile.HIGH)
    val performanceProfile: StateFlow<DevicePerformanceProfile> = _performanceProfile.asStateFlow()

    init {
        loadRecentFiles()
        loadFavoriteFiles()
        loadPerformanceProfile()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            getRecentFilesUseCase().collect { _recentFiles.value = it }
        }
    }

    private fun loadFavoriteFiles() {
        viewModelScope.launch {
            getFavoriteFilesUseCase().collect { _favoriteFiles.value = it }
        }
    }

    fun addRecentFile(file: PdfFile) {
        viewModelScope.launch {
            addRecentFileUseCase(file)
            loadRecentFiles()
        }
    }

    fun searchFiles(query: String) {
        viewModelScope.launch {
            searchFilesUseCase(query).collect { _searchResults.value = it }
        }
    }

    private fun loadPerformanceProfile() {
        viewModelScope.launch {
            getPerformanceProfileUseCase().collect { _performanceProfile.value = it }
        }
    }
}
