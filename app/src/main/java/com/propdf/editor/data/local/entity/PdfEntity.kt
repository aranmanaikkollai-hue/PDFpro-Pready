package com.propdf.editor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_documents")
data class PdfEntity(
    @PrimaryKey
    val uri: String,
    val name: String,
    val pageCount: Int = 0,
    val lastOpened: Long = System.currentTimeMillis()
)
