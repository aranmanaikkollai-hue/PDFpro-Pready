package com.propdf.editor.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.propdf.editor.data.local.entity.BookmarkEntity
import com.propdf.editor.data.local.entity.PdfEntity

@Database(
    entities = [PdfEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ProPDFDatabase : RoomDatabase() {
    // TODO: Add DAOs
    // abstract fun pdfDao(): PdfDao
    // abstract fun bookmarkDao(): BookmarkDao
}
