package com.propdf.editor.data.repository

import android.content.Context
import com.propdf.editor.data.local.dao.RecentFileDao
import com.propdf.editor.data.local.entity.RecentFileEntity
import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.repository.PdfRepository
import com.propdf.editor.utils.FileUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    private val recentFileDao: RecentFileDao,
    private val context: Context
) : PdfRepository {

    override suspend fun getPdfFiles(): Flow<List<PdfFile>> {
        return recentFileDao.getRecentFiles(100).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getRecentFiles(limit: Int): Flow<List<PdfFile>> {
        return recentFileDao.getRecentFiles(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getFavoriteFiles(): Flow<List<PdfFile>> {
        return recentFileDao.getFavoriteFiles().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun addRecentFile(pdfFile: PdfFile) {
        recentFileDao.insertOrUpdate(pdfFile.toEntity())
    }

    override suspend fun removeRecentFile(pdfFile: PdfFile) {
        recentFileDao.delete(pdfFile.toEntity())
    }

    override suspend fun toggleFavorite(pdfFile: PdfFile) {
        recentFileDao.updateFavoriteStatus(pdfFile.uri, !pdfFile.isFavorite)
    }

    override suspend fun searchFiles(query: String): Flow<List<PdfFile>> {
        return recentFileDao.searchFiles(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    private fun RecentFileEntity.toDomainModel(): PdfFile {
        return PdfFile(
            id = id,
            name = name,
            uri = uri,
            path = path,
            size = size,
            pageCount = pageCount,
            lastModified = lastModified,
            isFavorite = isFavorite,
            thumbnailPath = thumbnailPath
        )
    }

    private fun PdfFile.toEntity(): RecentFileEntity {
        return RecentFileEntity(
            id = id,
            name = name,
            uri = uri,
            path = path,
            size = size,
            pageCount = pageCount,
            lastModified = lastModified,
            isFavorite = isFavorite,
            thumbnailPath = thumbnailPath
        )
    }
}
