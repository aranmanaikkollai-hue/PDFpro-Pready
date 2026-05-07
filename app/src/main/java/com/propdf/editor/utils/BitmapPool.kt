package com.propdf.editor.utils

import android.graphics.Bitmap
import android.util.LruCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BitmapPool(maxSizeBytes: Int) {

    private val cache = object : LruCache<String, Bitmap>(maxSizeBytes) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }

        override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap?, newValue: Bitmap?) {
            oldValue?.recycleIfNotUsed()
        }
    }

    private val mutex = Mutex()
    private val usedBitmaps = mutableSetOf<Bitmap>()

    suspend fun get(key: String, width: Int, height: Int, config: Bitmap.Config): Bitmap? = mutex.withLock {
        val cached = cache.get(key)
        if (cached != null && !cached.isRecycled) {
            usedBitmaps.add(cached)
            return cached
        }
        try {
            val bitmap = Bitmap.createBitmap(width, height, config)
            usedBitmaps.add(bitmap)
            bitmap
        } catch (e: OutOfMemoryError) {
            cache.trimToSize(cache.size() / 2)
            null
        }
    }

    suspend fun release(bitmap: Bitmap?) = mutex.withLock {
        bitmap?.let {
            usedBitmaps.remove(it)
            // Don't recycle immediately, keep in cache for reuse
        }
    }

    suspend fun clear() = mutex.withLock {
        usedBitmaps.clear()
        cache.evictAll()
    }

    private fun Bitmap.recycleIfNotUsed() {
        if (!usedBitmaps.contains(this) && !isRecycled) {
            recycle()
        }
    }

    companion object {
        fun create(context: android.content.Context): BitmapPool {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val memoryInfo = android.app.ActivityManager.MemoryInfo()
            am.getMemoryInfo(memoryInfo)
            val maxMemory = (memoryInfo.totalMem * 0.1).toInt() // 10% of total RAM
            return BitmapPool(maxMemory.coerceAtLeast(8 * 1024 * 1024)) // Min 8MB
        }
    }
}
