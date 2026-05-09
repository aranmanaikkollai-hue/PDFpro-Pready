package com.propdf.editor.data.repository

import android.net.Uri
import com.propdf.editor.data.local.dao.FavoriteDao
import com.propdf.editor.data.local.dao.PdfDao
import com.propdf.editor.data.local.dao.RecentFileDao
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
        recentFileDao.getAll().map { list ->
            list.map { entity ->
                PdfDocument(
                    Uri.parse(entity.uri),
                    entity.name,
                    0,
                    lastOpened = entity.lastOpened
                )
            }
        }

    override suspend fun insertDocument(document: PdfDocument) {
        pdfDao.insert(PdfEntity(document.uri.toString(), document.name, document.pageCount, document.lastOpened))
        recentFileDao.insert(RecentFileEntity(document.uri.toString(), document.name, document.lastOpened))
    }

    override suspend fun deleteDocument(uri: String) {
        pdfDao.delete(PdfEntity(uri, "", 0))
        recentFileDao.deleteByUri(uri)
        favoriteDao.deleteByUri(uri)
    }

    override fun searchDocuments(query: String): Flow<List<PdfDocument>> =
        pdfDao.search(query).map { list ->
            list.map { entity ->
                PdfDocument(
                    Uri.parse(entity.uri),
                    entity.name,
                    entity.pageCount,
                    lastOpened = entity.lastOpened
                )
            }
        }

    override suspend fun toggleFavorite(uri: String) {
        // TODO: Implement proper toggle with existence check via DAO
    }

    override fun getFavoriteDocuments(): Flow<List<PdfDocument>> =
        favoriteDao.getAll().map { list ->
            list.map { entity ->
                PdfDocument(
                    Uri.parse(entity.uri),
                    entity.name,
                    0,
                    lastOpened = entity.addedAt
                )
            }
        }
}
