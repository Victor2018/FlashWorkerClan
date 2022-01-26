package com.flash.worker.module.message.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BitmapDecoder
 * Author: Victor
 * Date: 2021/4/27 11:58
 * Description: 
 * -----------------------------------------------------------------
 */
object BitmapDecoder {
    fun decode(`is`: InputStream): Bitmap? {
        val options =
            BitmapFactory.Options()

        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565
        /**
         * 在4.4上，如果之前is标记被移动过，会导致解码失败
         */
        try {
            if (`is`.markSupported()) {
                `is`.reset()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            return BitmapFactory.decodeStream(`is`, null, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return null
    }

}