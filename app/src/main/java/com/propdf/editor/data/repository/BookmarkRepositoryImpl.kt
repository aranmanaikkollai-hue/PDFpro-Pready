package com.propdf.editor.data.repository

import com.propdf.editor.data.local.dao.BookmarkDao
import com.propdf.editor.data.local.entity.BookmarkEntity
import com.propdf.editor.domain.model.PdfBookmark
import com.propdf.editor.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun getBookmarks(pdfUri: String): Flow<List<PdfBookmark>> {
        return bookmarkDao.getBookmarks(pdfUri).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addBookmark(bookmark: PdfBookmark) {
        bookmarkDao.insert(bookmark.toEntity())
    }

    override suspend fun removeBookmark(bookmarkId: Long) {
        bookmarkDao.delete(bookmarkId)
    }

    override suspend fun updateBookmark(bookmark: PdfBookmark) {
        bookmarkDao.update(bookmark.toEntity())
    }

    private fun BookmarkEntity.toDomainModel(): PdfBookmark {
        return PdfBookmark(
            id = id,
            pdfUri = pdfUri,
            pageNumber = pageNumber,
            title = title,
            timestamp = timestamp
        )
    }

    private fun PdfBookmark.toEntity(): BookmarkEntity {
        return BookmarkEntity(
            id = id,
            pdfUri = pdfUri,
            pageNumber = pageNumber,
            title = title,
            timestamp = timestamp
        )
    }
}
