package com.propdf.editor.data.repository

import com.propdf.editor.data.local.dao.FavoriteDao
import com.propdf.editor.data.local.dao.PdfDao
import com.propdf.editor.data.local.dao.RecentFileDao
import com.propdf.editor.data.local.entity.FavoriteEntity
import com.propdf.editor.data.local.entity.PdfEntity
import com.propdf.editor.data.local.entity.RecentFileEntity
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    private val pdfDao: PdfDao,
    private val recentFileDao: RecentFileDao,
    private val favoriteDao: FavoriteDao
) : PdfRepository {

    override fun getRecentDocuments(): Flow<List<PdfDocument>> =
        recentFileDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertDocument(document: PdfDocument) {
        pdfDao.insert(document.toEntity())
        recentFileDao.insert(RecentFileEntity(document.uri.toString(), document.name, document.lastOpened))
    }

    override suspend fun deleteDocument(uri: String) {
        pdfDao.delete(PdfEntity(uri, "", 0))
        recentFileDao.deleteByUri(uri)
        favoriteDao.deleteByUri(uri)
    }

    override fun searchDocuments(query: String): Flow<List<PdfDocument>> =
        pdfDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun toggleFavorite(uri: String) {
        val existing = favoriteDao.getAll().let { flow ->
            // Simplified: check in ViewModel or use a direct query
        }
    }

    override fun getFavoriteDocuments(): Flow<List<PdfDocument>> =
        favoriteDao.getAll().map { list -> list.map { PdfDocument(android.net.Uri.parse(it.uri), it.name, 0, lastOpened = it.addedAt) } }

    private fun PdfEntity.toDomain() = PdfDocument(android.net.Uri.parse(uri), name, pageCount, lastOpened = lastOpened)
    private fun PdfDocument.toEntity() = PdfEntity(uri.toString(), name, pageCount, lastOpened)
}
