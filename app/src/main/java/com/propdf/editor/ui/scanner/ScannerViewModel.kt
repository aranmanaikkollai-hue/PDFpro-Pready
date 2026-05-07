package com.propdf.editor.ui.scanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setProcessing(processing: Boolean) {
        _isProcessing.value = processing
    }

    fun setError(error: String?) {
        _error.value = error
    }
}
