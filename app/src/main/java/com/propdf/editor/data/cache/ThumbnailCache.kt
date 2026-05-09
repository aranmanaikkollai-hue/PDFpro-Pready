package com.propdf.editor.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.propdf.editor.util.BitmapUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThumbnailCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val memoryCache: LruCache<String, Bitmap>
    private val cacheDir = File(context.cacheDir, "thumbnails").apply { mkdirs() }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int = bitmap.byteCount / 1024
        }
    }

    fun get(uri: String): Bitmap? = memoryCache.get(uri)

    suspend fun put(uri: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        memoryCache.put(uri, bitmap)
        val file = File(cacheDir, uri.hashCode().toString())
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }
    }

    suspend fun getOrGenerate(uri: String, generator: suspend () -> Bitmap?): Bitmap? {
        memoryCache.get(uri)?.let { return it }
        val file = File(cacheDir, uri.hashCode().toString())
        if (file.exists()) {
            return BitmapUtils.decodeSampled(file.absolutePath, 200, 300).also {
                if (it != null) memoryCache.put(uri, it)
            }
        }
        return generator()?.also { put(uri, it) }
    }

    fun clear() {
        memoryCache.evictAll()
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}
