package com.propdf.editor.di

import android.content.Context
import com.propdf.editor.data.local.dao.*
import com.propdf.editor.data.local.db.ProPDFDatabase
import com.propdf.editor.data.repository.*
import com.propdf.editor.domain.repository.*
import com.propdf.editor.ocr.TesseractOcrEngine
import com.propdf.editor.pdf.PdfBoxOperations
import com.propdf.editor.pdf.PdfiumEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProPDFDatabase {
        return ProPDFDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRecentFileDao(database: ProPDFDatabase): RecentFileDao {
        return database.recentFileDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: ProPDFDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideAnnotationDao(database: ProPDFDatabase): AnnotationDao {
        return database.annotationDao()
    }

    @Provides
    @Singleton
    fun providePdfRepository(
        recentFileDao: RecentFileDao,
        @ApplicationContext context: Context
    ): PdfRepository {
        return PdfRepositoryImpl(recentFileDao, context)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao
    ): BookmarkRepository {
        return BookmarkRepositoryImpl(bookmarkDao)
    }

    @Provides
    @Singleton
    fun provideAnnotationRepository(
        annotationDao: AnnotationDao
    ): AnnotationRepository {
        return AnnotationRepositoryImpl(annotationDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providePdfViewerRepository(
        @ApplicationContext context: Context
    ): PdfViewerRepository {
        return PdfiumEngine(context)
    }

    @Provides
    @Singleton
    fun providePdfOperationsRepository(
        @ApplicationContext context: Context
    ): PdfOperationsRepository {
        return PdfBoxOperations(context)
    }

    @Provides
    @Singleton
    fun provideOcrRepository(
        @ApplicationContext context: Context
    ): OcrRepository {
        return TesseractOcrEngine(context)
    }
}
