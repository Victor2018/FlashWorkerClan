package com.flash.worker.lib.luban

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.flash.worker.lib.common.util.CacheCleanUtils
import com.flash.worker.lib.common.util.Loger
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompressModule
 * Author: Victor
 * Date: 2020/12/23 19:18
 * Description: 
 * -----------------------------------------------------------------
 */
class CompressModule(var config: CompressConfig?) {
    companion object {
        private const val TAG = "CompressModule"
        const val FIRST_GEAR = 1 // 一档

        const val THIRD_GEAR = 3 // 三档

        const val CUSTOM_GEAR = 4 // 四档
    }


    private var mByteArrayOutputStream: ByteArrayOutputStream? = null


    @Throws(IOException::class)
    fun compressImage(file: File): File? {
        if (config == null) {
            throw NullPointerException("config cannot be null")
        }
        // 网络图片返回原图
        val eqHttp = isHttp(file.path)
        return if (file != null && eqHttp) {
            file
        } else when (config?.grade) {
            THIRD_GEAR -> thirdCompress(file)
            CUSTOM_GEAR -> customCompress(file)
            FIRST_GEAR -> firstCompress(file)
            else -> file
        }
    }

    @Throws(IOException::class)
    private fun thirdCompress(file: File): File? {
        if (config == null) {
            throw NullPointerException("mCompressConfig cannot be null")
        }
        val file_name = file.name
        val thumb =
            CompressCacheUtil.getCacheFilePath(config?.compressFormat, file_name)
        var size: Double
        val filePath = file.absolutePath
        val angle = CompressImageUtil.getImageSpinAngle(filePath)
        var width = CompressImageUtil.getImageSize(filePath)?.get(0) ?: 0
        var height = CompressImageUtil.getImageSize(filePath)?.get(1) ?: 0
        val flip = width > height
        var thumbW = if (width % 2 == 1) width + 1 else width
        var thumbH = if (height % 2 == 1) height + 1 else height
        width = if (thumbW > thumbH) thumbH else thumbW
        height = if (thumbW > thumbH) thumbW else thumbH
        val scale = width.toDouble() / height
        if (scale <= 1 && scale > 0.5625) {
            if (height < 1664) {
                if (file.length() / 1024 < 150) {
                    return file
                }
                size = width * height / Math.pow(1664.0, 2.0) * 150
                size = if (size < 60) 60.0 else size
            } else if (height >= 1664 && height < 4990) {
                thumbW = width / 2
                thumbH = height / 2
                size = thumbW * thumbH / Math.pow(2495.0, 2.0) * 300
                size = if (size < 60) 60.0 else size
            } else if (height >= 4990 && height < 10240) {
                thumbW = width / 4
                thumbH = height / 4
                size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                size = if (size < 100) 100.0 else size
            } else {
                val multiple = if (height / 1280 == 0) 1 else height / 1280
                thumbW = width / multiple
                thumbH = height / multiple
                size = thumbW * thumbH / Math.pow(2560.0, 2.0) * 300
                size = if (size < 100) 100.0 else size
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (height < 1280 && file.length() / 1024 < 200) {
                return file
            }
            val multiple = if (height / 1280 == 0) 1 else height / 1280
            thumbW = width / multiple
            thumbH = height / multiple
            size = thumbW * thumbH / (1440.0 * 2560.0) * 400
            size = if (size < 100) 100.0 else size
        } else {
            val multiple = Math.ceil(height / (1280.0 / scale)).toInt()
            thumbW = width / multiple
            thumbH = height / multiple
            size = thumbW * thumbH / (1280.0 * (1280 / scale)) * 500
            size = if (size < 100) 100.0 else size
        }
        return compress(
            filePath, thumb, if (flip) thumbH else thumbW, if (flip) thumbW else thumbH, angle,
            size.toLong()
        )
    }

    @Throws(IOException::class)
    private fun firstCompress(file: File): File? {
        if (config == null) {
            throw NullPointerException("mCompressConfig cannot be null")
        }
        val minSize = 60
        val longSide = 720
        val shortSide = 1280
        val file_name = file.name
        val thumbFilePath =
            CompressCacheUtil.getCacheFilePath(config!!.compressFormat, file_name)
        val filePath = file.absolutePath
        var size: Long = 0
        val maxSize = file.length() / 5
        val angle = CompressImageUtil.getImageSpinAngle(filePath)
        val imgSize = CompressImageUtil.getImageSize(filePath)
        var width = 0
        var height = 0
        if (imgSize!![0] <= imgSize[1]) {
            val scale =
                imgSize[0].toDouble() / imgSize[1].toDouble()
            if (scale <= 1.0 && scale > 0.5625) {
                width = if (imgSize[0] > shortSide) shortSide else imgSize[0]
                height = width * imgSize[1] / imgSize[0]
                size = minSize.toLong()
            } else if (scale <= 0.5625) {
                height = if (imgSize[1] > longSide) longSide else imgSize[1]
                width = height * imgSize[0] / imgSize[1]
                size = maxSize
            }
        } else {
            val scale =
                imgSize[1].toDouble() / imgSize[0].toDouble()
            if (scale <= 1.0 && scale > 0.5625) {
                height = if (imgSize[1] > shortSide) shortSide else imgSize[1]
                width = height * imgSize[0] / imgSize[1]
                size = minSize.toLong()
            } else if (scale <= 0.5625) {
                width = if (imgSize[0] > longSide) longSide else imgSize[0]
                height = width * imgSize[1] / imgSize[0]
                size = maxSize
            }
        }
        return compress(filePath, thumbFilePath, width, height, angle, size)
    }

    @Throws(IOException::class)
    fun customCompress(file: File): File? {
        if (config == null) {
            throw NullPointerException("mCompressConfig cannot be null")
        }
        val fileName = file.name
        val thumbFilePath =
            CompressCacheUtil.getCacheFilePath(config!!.compressFormat, fileName)
        val filePath = file.absolutePath
        val angle = CompressImageUtil.getImageSpinAngle(filePath)
        val fileSize =
            if (config!!.maxSize > 0 && config!!.maxSize < file.length() / 1024) config!!.maxSize.toLong() else file.length() / 1024
        val size = CompressImageUtil.getImageSize(filePath)
        var width = size!![0]
        var height = size[1]
        if (config!!.maxSize > 0 && config!!.maxSize < file.length() / 1024f) {
            // find a suitable size
            val scale =
                Math.sqrt((file.length() / 1024f / config!!.maxSize).toDouble()).toFloat()
            width = (width / scale).toInt()
            height = (height / scale).toInt()
        }

        // check the width&height
        if (config!!.maxWidth > 0) {
            width = Math.min(width, config!!.maxWidth)
        }
        if (config!!.maxHeight > 0) {
            height = Math.min(height, config!!.maxHeight)
        }
        val scale = Math.min(
            width.toFloat() / size[0],
            height.toFloat() / size[1]
        )
        width = (size[0] * scale).toInt()
        height = (size[1] * scale).toInt()

        // 不压缩
        return if (config!!.maxSize > file.length() / 1024f && scale == 1f) {
            file
        } else compress(filePath, thumbFilePath, width, height, angle, fileSize)
    }

    @Throws(IOException::class)
    private fun compress(
        largeImagePath: String, thumbFilePath: String?, width: Int, height: Int,
        angle: Int, size: Long
    ): File? {
        var thbBitmap = compress(largeImagePath, width, height)
        thbBitmap = CompressImageUtil.rotatingImage(angle, thbBitmap!!)
        return saveImage(thumbFilePath, thbBitmap, size)
    }

    private fun compress(
        imagePath: String,
        width: Int,
        height: Int
    ): Bitmap? {
        val options =
            BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        val outH = options.outHeight
        val outW = options.outWidth
        var inSampleSize = 1
        while (outH / inSampleSize > height || outW / inSampleSize > width) {
            inSampleSize *= 2
        }
        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(imagePath, options)
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap   the image what be save   目标图片
     * @param size     the file size of image   期望大小
     */
    @Throws(IOException::class)
    private fun saveImage(
        filePath: String?,
        bitmap: Bitmap?,
        size: Long
    ): File? {
        if (config == null) {
            throw NullPointerException("mCompressConfig cannot be null")
        }
        if (bitmap == null) {
            throw NullPointerException("bitmap cannot be null")
        }
        val result = File(filePath!!.substring(0, filePath.lastIndexOf("/")))
        if (!result.exists() && !result.mkdirs()) {
            return null
        }
        if (mByteArrayOutputStream == null) {
            mByteArrayOutputStream = ByteArrayOutputStream(
                bitmap.width * bitmap.height
            )
        } else {
            mByteArrayOutputStream!!.reset()
        }
        var options = 100
        bitmap.compress(config!!.compressFormat, options, mByteArrayOutputStream)
        while (mByteArrayOutputStream!!.size() / 1024 > size && options > 6) {
            mByteArrayOutputStream!!.reset()
            options -= 6
            bitmap.compress(config!!.compressFormat, options, mByteArrayOutputStream)
        }
        bitmap.recycle()
        val fos = FileOutputStream(filePath)
        mByteArrayOutputStream!!.writeTo(fos)
        fos.close()
        val file = File(filePath)
        Loger.e(TAG, "saveImage-fileSize = " + CacheCleanUtils.getFormatSize(file.length().toDouble()))
        return file
    }

    fun isHttp(path: String): Boolean {
        return !TextUtils.isEmpty(path) && (path.startsWith("http") || path.startsWith(
            "https"
        ))
    }
}