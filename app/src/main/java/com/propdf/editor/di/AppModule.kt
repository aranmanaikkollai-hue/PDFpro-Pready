package com.propdf.editor.di

import android.content.Context
import androidx.room.Room
import com.propdf.editor.data.local.db.ProPDFDatabase
import com.propdf.editor.data.ocr.TesseractOcrEngine
import com.propdf.editor.data.pdfium.PdfiumEngine
import com.propdf.editor.data.repository.OcrRepositoryImpl
import com.propdf.editor.data.repository.PdfViewerRepositoryImpl
import com.propdf.editor.domain.repository.OcrRepository
import com.propdf.editor.domain.repository.PdfViewerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindPdfViewerRepository(
        impl: PdfViewerRepositoryImpl
    ): PdfViewerRepository

    @Binds
    @Singleton
    abstract fun bindOcrRepository(
        impl: OcrRepositoryImpl
    ): OcrRepository

    companion object {

        @Provides
        @Singleton
        fun provideProPDFDatabase(
            @ApplicationContext context: Context
        ): ProPDFDatabase {
            return Room.databaseBuilder(
                context,
                ProPDFDatabase::class.java,
                "propdf_database"
            ).build()
        }

        @Provides
        @Singleton
        fun providePdfiumEngine(
            @ApplicationContext context: Context
        ): PdfiumEngine = PdfiumEngine(context)

        @Provides
        @Singleton
        fun provideTesseractOcrEngine(
            @ApplicationContext context: Context
        ): TesseractOcrEngine = TesseractOcrEngine(context)
    }
}
