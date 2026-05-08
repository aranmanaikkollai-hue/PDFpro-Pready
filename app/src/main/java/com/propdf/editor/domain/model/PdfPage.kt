package com.propdf.editor.domain.model

import android.graphics.Bitmap

data class PdfPage(
    val index: Int,
    val bitmap: Bitmap? = null,
    val width: Int = 0,
    val height: Int = 0
)
