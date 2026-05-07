package com.propdf.editor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "recent_files",
    indices = [Index(value = ["uri"], unique = true)]
)
data class RecentFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val uri: String,
    val path: String? = null,
    val size: Long = 0,
    val pageCount: Int = 0,
    val lastModified: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val thumbnailPath: String? = null
)

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["pdfUri", "pageNumber"], unique = true)]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pdfUri: String,
    val pageNumber: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "annotations",
    indices = [Index(value = ["pdfUri", "pageNumber"])]
)
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pdfUri: String,
    val pageNumber: Int,
    val type: com.propdf.editor.domain.model.AnnotationType,
    val color: Int,
    val data: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "favorites",
    indices = [Index(value = ["uri"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val uri: String,
    val path: String? = null,
    val size: Long = 0,
    val pageCount: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
