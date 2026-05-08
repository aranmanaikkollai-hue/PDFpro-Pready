package com.propdf.editor.domain.model

data class Bookmark(
    val id: Long = 0,
    val documentUri: String,
    val pageIndex: Int,
    val label: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
