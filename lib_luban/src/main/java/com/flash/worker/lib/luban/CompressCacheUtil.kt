package com.flash.worker.lib.luban

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.flash.worker.lib.common.app.App
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompressCacheUtil
 * Author: Victor
 * Date: 2020/12/23 19:17
 * Description: 
 * -----------------------------------------------------------------
 */
object CompressCacheUtil {
    private const val TAG = "CompressCacheUtil"
    private const val DEFAULT_DISK_CACHE_DIR = "luban_disk_cache"

    fun getCacheFilePath(
        compressFormat: Bitmap.CompressFormat?,
        fileName: String
    ): String? {
        val name =
            StringBuilder("Luban_" + System.currentTimeMillis())
        if (compressFormat == Bitmap.CompressFormat.WEBP) {
            name.append(".webp")
        } else if (fileName.endsWith(".webp")) {
            name.append(".webp")
        } else if (fileName.endsWith(".png")) {
            name.append(".png")
        } else {
            name.append(".jpg")
        }
        return getPhotoCacheDir(App.get())?.absolutePath + File.separator + name
    }

    fun getPhotoCacheDir(context: Context): File? {
        return getPhotoCacheDir(
            context,
            DEFAULT_DISK_CACHE_DIR
        )
    }

    private fun getPhotoCacheDir(
        context: Context,
        cacheName: String
    ): File? {
        val cacheDir = context.cacheDir
        if (cacheDir != null) {
            val result = File(cacheDir, cacheName)
            return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) {
                // File wasn't able to create a directory, or the result exists but not a directory
                null
            } else result
        }
        Log.e(TAG, "default disk cache dir is null")
        return null
    }
}