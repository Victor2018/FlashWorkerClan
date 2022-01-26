package com.flash.worker.lib.luban

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompressImageUtil
 * Author: Victor
 * Date: 2020/12/23 19:18
 * Description: 
 * -----------------------------------------------------------------
 */
object CompressImageUtil {
    fun rotatingImage(angle: Int, bitmap: Bitmap): Bitmap? {
        //rotate image
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())

        //create a new image
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix,
            true
        )
    }

    /**
     * obtain the image's width and height
     *
     * @param imagePath the path of image
     */
    fun getImageSize(imagePath: String?): IntArray? {
        val res = IntArray(2)
        val options =
            BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 1
        BitmapFactory.decodeFile(imagePath, options)
        res[0] = options.outWidth
        res[1] = options.outHeight
        return res
    }

    @Throws(IOException::class)
    fun getImageSpinAngle(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return degree
        }
        return degree
    }
}