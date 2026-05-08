package com.propdf.editor.domain.usecase

import com.propdf.editor.domain.model.PdfFile
import com.propdf.editor.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentFilesUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(): Flow<List<PdfFile>> = repository.getRecentDocuments().map { docs ->
        docs.map { PdfFile(it.uri, it.name, lastModified = it.lastOpened) }
    }
}

class GetFavoriteFilesUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(): Flow<List<PdfFile>> = repository.getFavoriteDocuments().map { docs ->
        docs.map { PdfFile(it.uri, it.name, lastModified = it.lastOpened, isFavorite = true) }
    }
}

class AddRecentFileUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(file: PdfFile) {
        repository.insertDocument(
            com.propdf.editor.domain.model.PdfDocument(
                uri = file.uri,
                name = file.name,
                pageCount = 0,
                lastOpened = file.lastModified
            )
        )
    }
}

class SearchFilesUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(query: String): Flow<List<PdfFile>> = repository.searchDocuments(query).map { docs ->
        docs.map { PdfFile(it.uri, it.name, lastModified = it.lastOpened) }
    }
}
