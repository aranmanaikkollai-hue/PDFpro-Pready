package com.propdf.editor.data.repository

import com.propdf.editor.domain.model.Bookmark
import com.propdf.editor.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {
    override suspend fun addBookmark(bookmark: Bookmark) {}
    override suspend fun removeBookmark(id: Long) {}
    override fun getBookmarksForDocument(documentUri: String): Flow<List<Bookmark>> = flow { emit(emptyList()) }
}
