package com.propdf.editor.data.local.dao

import androidx.room.*
import com.propdf.editor.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDao {
    @Query("SELECT * FROM pdf_documents ORDER BY lastOpened DESC")
    fun getAll(): Flow<List<PdfEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PdfEntity)

    @Delete
    suspend fun delete(entity: PdfEntity)

    @Query("SELECT * FROM pdf_documents WHERE name LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PdfEntity>>
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE documentUri = :documentUri")
    fun getByDocument(documentUri: String): Flow<List<BookmarkEntity>>

    @Insert
    suspend fun insert(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastOpened DESC")
    fun getAll(): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)
}

@Dao
interface AnnotationDao {
    @Query("SELECT * FROM annotations WHERE documentUri = :documentUri")
    fun getByDocument(documentUri: String): Flow<List<AnnotationEntity>>

    @Insert
    suspend fun insert(entity: AnnotationEntity)

    @Query("DELETE FROM annotations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Update
    suspend fun update(entity: AnnotationEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)
}
