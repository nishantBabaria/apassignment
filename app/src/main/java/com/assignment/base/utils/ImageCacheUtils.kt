package com.assignment.base.utils

import android.graphics.Bitmap
import androidx.collection.LruCache

object ImageCacheUtils {
    val memoryCache: LruCache<String, Bitmap> by lazy {
        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024) / 8
        LruCache<String, Bitmap>(cacheSize.toInt())
    }
}