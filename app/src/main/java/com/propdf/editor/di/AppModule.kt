package com.propdf.editor.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.propdf.editor.data.cache.ThumbnailCache
import com.propdf.editor.data.local.db.ProPDFDatabase
import com.propdf.editor.data.local.prefs.SettingsDataStore
import com.propdf.editor.data.ocr.TesseractOcrEngine
import com.propdf.editor.data.pdfium.PdfiumEngine
import com.propdf.editor.data.repository.*
import com.propdf.editor.domain.repository.*
import com.propdf.editor.pdf.PdfBoxOperations
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds @Singleton abstract fun bindPdfViewerRepository(impl: PdfViewerRepositoryImpl): PdfViewerRepository
    @Binds @Singleton abstract fun bindOcrRepository(impl: OcrRepositoryImpl): OcrRepository
    @Binds @Singleton abstract fun bindPdfRepository(impl: PdfRepositoryImpl): PdfRepository
    @Binds @Singleton abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository
    @Binds @Singleton abstract fun bindAnnotationRepository(impl: AnnotationRepositoryImpl): AnnotationRepository
    @Binds @Singleton abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
    @Binds @Singleton abstract fun bindPdfOperationsRepository(impl: PdfBoxOperations): PdfOperationsRepository

    companion object {
        @Provides @Singleton
        fun provideProPDFDatabase(@ApplicationContext context: Context): ProPDFDatabase =
            Room.databaseBuilder(context, ProPDFDatabase::class.java, "propdf_database")
                .fallbackToDestructiveMigration()
                .build()

        @Provides @Singleton
        fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
            SettingsDataStore(context)

        @Provides @Singleton
        fun providePdfiumEngine(@ApplicationContext context: Context): PdfiumEngine =
            PdfiumEngine(context)

        @Provides @Singleton
        fun provideTesseractOcrEngine(@ApplicationContext context: Context): TesseractOcrEngine =
            TesseractOcrEngine(context)

        @Provides @Singleton
        fun provideThumbnailCache(@ApplicationContext context: Context): ThumbnailCache =
            ThumbnailCache(context)

        @Provides @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @Provides @Singleton
        fun provideIoDispatcher(): CoroutineContext = Dispatchers.IO
    }
}
