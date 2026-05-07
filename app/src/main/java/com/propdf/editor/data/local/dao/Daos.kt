package com.propdf.editor.data.local.dao

import androidx.room.*
import com.propdf.editor.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastModified DESC LIMIT :limit")
    fun getRecentFiles(limit: Int): Flow<List<RecentFileEntity>>

    @Query("SELECT * FROM recent_files WHERE isFavorite = 1 ORDER BY lastModified DESC")
    fun getFavoriteFiles(): Flow<List<RecentFileEntity>>

    @Query("SELECT * FROM recent_files WHERE name LIKE '%' || :query || '%' ORDER BY lastModified DESC")
    fun searchFiles(query: String): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(file: RecentFileEntity)

    @Query("UPDATE recent_files SET isFavorite = :isFavorite WHERE uri = :uri")
    suspend fun updateFavoriteStatus(uri: String, isFavorite: Boolean)

    @Delete
    suspend fun delete(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE lastModified < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM recent_files")
    suspend fun getCount(): Int
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE pdfUri = :pdfUri ORDER BY pageNumber")
    fun getBookmarks(pdfUri: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun delete(bookmarkId: Long)

    @Update
    suspend fun update(bookmark: BookmarkEntity)
}

@Dao
interface AnnotationDao {
    @Query("SELECT * FROM annotations WHERE pdfUri = :pdfUri AND pageNumber = :pageNumber")
    suspend fun getAnnotations(pdfUri: String, pageNumber: Int): List<AnnotationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(annotation: AnnotationEntity)

    @Query("DELETE FROM annotations WHERE id = :annotationId")
    suspend fun delete(annotationId: Long)

    @Update
    suspend fun update(annotation: AnnotationEntity)

    @Query("DELETE FROM annotations WHERE pdfUri = :pdfUri")
    suspend fun deleteAllForPdf(pdfUri: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE uri = :uri)")
    suspend fun isFavorite(uri: String): Boolean
}
