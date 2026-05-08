package com.propdf.editor.data.repository

import com.propdf.editor.data.local.entity.BookmarkEntity
import com.propdf.editor.domain.model.Bookmark
import com.propdf.editor.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {

    override suspend fun addBookmark(bookmark: Bookmark) {
        val entity = BookmarkEntity(
            documentUri = bookmark.documentUri,
            pageIndex = bookmark.pageIndex,
            label = bookmark.label,
            createdAt = bookmark.createdAt
        )
        // TODO: Insert via DAO
    }

    override suspend fun removeBookmark(id: Long) {
        // TODO: Delete via DAO
    }

    override fun getBookmarksForDocument(documentUri: String): Flow<List<Bookmark>> = flow {
        // TODO: Query via DAO
        emit(emptyList())
    }
}
