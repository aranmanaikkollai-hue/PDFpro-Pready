package com.propdf.editor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_documents")
data class PdfEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val pageCount: Int = 0,
    val lastOpened: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentUri: String,
    val pageIndex: Int,
    val label: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val lastOpened: Long = System.currentTimeMillis()
)

@Entity(tableName = "annotations")
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentUri: String,
    val pageIndex: Int,
    val type: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Int,
    val content: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val addedAt: Long = System.currentTimeMillis()
)
