package com.propdf.editor.di

import android.content.Context
import com.propdf.editor.data.local.db.ProPDFDatabase
import com.propdf.editor.ocr.TesseractOcrEngine
import com.propdf.editor.pdf.PdfiumEngine
import com.propdf.editor.repository.OcrRepository
import com.propdf.editor.repository.PdfViewerRepository
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
    fun provideProPDFDatabase(@ApplicationContext context: Context): ProPDFDatabase {
        return ProPDFDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePdfiumEngine(@ApplicationContext context: Context): PdfiumEngine {
        return PdfiumEngine(context)
    }

    @Provides
    @Singleton
    fun providePdfViewerRepository(engine: PdfiumEngine): PdfViewerRepository {
        return PdfViewerRepository(engine)
    }

    @Provides
    @Singleton
    fun provideTesseractOcrEngine(@ApplicationContext context: Context): TesseractOcrEngine {
        return TesseractOcrEngine(context)
    }

    @Provides
    @Singleton
    fun provideOcrRepository(engine: TesseractOcrEngine): OcrRepository {
        return OcrRepository(engine)
    }
}
