package com.propdf.editor.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.propdf.editor.data.local.dao.*
import com.propdf.editor.data.local.entity.*

@Database(
    entities = [
        PdfEntity::class,
        BookmarkEntity::class,
        RecentFileEntity::class,
        AnnotationEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ProPDFDatabase : RoomDatabase() {
    abstract fun pdfDao(): PdfDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun recentFileDao(): RecentFileDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun favoriteDao(): FavoriteDao
}
