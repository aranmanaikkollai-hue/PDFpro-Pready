package com.propdf.editor.data.repository

import com.propdf.editor.domain.model.PdfAnnotation
import com.propdf.editor.domain.repository.AnnotationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnotationRepositoryImpl @Inject constructor() : AnnotationRepository {
    override fun getAnnotations(documentUri: String): Flow<List<PdfAnnotation>> = flow { emit(emptyList()) }
    override suspend fun addAnnotation(annotation: PdfAnnotation) {}
    override suspend fun removeAnnotation(id: Long) {}
    override suspend fun updateAnnotation(annotation: PdfAnnotation) {}
}
