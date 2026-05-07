package com.propdf.editor.data.repository

import com.propdf.editor.data.local.dao.AnnotationDao
import com.propdf.editor.data.local.entity.AnnotationEntity
import com.propdf.editor.domain.model.PdfAnnotation
import com.propdf.editor.domain.repository.AnnotationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnotationRepositoryImpl @Inject constructor(
    private val annotationDao: AnnotationDao
) : AnnotationRepository {

    override suspend fun getAnnotations(pdfUri: String, pageNumber: Int): List<PdfAnnotation> {
        return annotationDao.getAnnotations(pdfUri, pageNumber).map { it.toDomainModel() }
    }

    override suspend fun addAnnotation(annotation: PdfAnnotation) {
        annotationDao.insert(annotation.toEntity())
    }

    override suspend fun removeAnnotation(annotationId: Long) {
        annotationDao.delete(annotationId)
    }

    override suspend fun updateAnnotation(annotation: PdfAnnotation) {
        annotationDao.update(annotation.toEntity())
    }

    private fun AnnotationEntity.toDomainModel(): PdfAnnotation {
        return PdfAnnotation(
            id = id,
            pdfUri = pdfUri,
            pageNumber = pageNumber,
            type = type,
            color = color,
            data = data,
            timestamp = timestamp
        )
    }

    private fun PdfAnnotation.toEntity(): AnnotationEntity {
        return AnnotationEntity(
            id = id,
            pdfUri = pdfUri,
            pageNumber = pageNumber,
            type = type,
            color = color,
            data = data,
            timestamp = timestamp
        )
    }
}
