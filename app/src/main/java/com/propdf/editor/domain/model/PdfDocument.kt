package com.propdf.editor.domain.model

import android.net.Uri

data class PdfDocument(
    val uri: Uri,
    val name: String,
    val pageCount: Int,
    val path: String? = null,
    val lastOpened: Long = System.currentTimeMillis()
)
