package com.propdf.editor.domain.model

import android.graphics.Bitmap
import android.net.Uri

data class ScanPage(
    val id: Int = 0,
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val filter: ScanFilter = ScanFilter.AUTO
)

enum class ScanFilter {
    AUTO, COLOR, GRAYSCALE, BLACK_WHITE
}
