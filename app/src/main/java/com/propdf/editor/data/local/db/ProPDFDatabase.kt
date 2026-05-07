package com.propdf.editor.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.propdf.editor.data.local.dao.*
import com.propdf.editor.data.local.entity.*

@Database(
    entities = [
        RecentFileEntity::class,
        BookmarkEntity::class,
        AnnotationEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ProPDFDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private const val DATABASE_NAME = "propdf_database"

        @Volatile
        private var instance: ProPDFDatabase? = null

        fun getInstance(context: Context): ProPDFDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ProPDFDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ProPDFDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
