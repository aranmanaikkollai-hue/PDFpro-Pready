package com.propdf.editor.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

class BitmapPool(context: Context) {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val bitmapCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun get(key: String): Bitmap? = bitmapCache.get(key)

    fun put(key: String, bitmap: Bitmap) {
        if (bitmapCache.get(key) == null) {
            bitmapCache.put(key, bitmap)
        }
    }

    fun clear() {
        bitmapCache.evictAll()
    }
}
