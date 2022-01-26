package com.flash.worker.lib.luban

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.MainHandler
import com.luck.picture.lib.entity.LocalMedia
import java.io.File
import java.io.IOException


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompressHelper
 * Author: Victor
 * Date: 2020/12/23 19:21
 * Description: 
 * -----------------------------------------------------------------
 */
class CompressHelper(var config: CompressConfig?, var listener: OnCompressListener) {
    private val TAG = "CompressHelper"
    val COMPRESS_IMAGE = 0x801
    private val maxSizeKb = 800 //超过800KB才压缩

    private var mCompressModule: CompressModule? = null

    private var mCompressHandler: Handler? = null
    private var mCompressHandlerThread: HandlerThread? = null
    private var localMediaList: MutableList<LocalMedia>? = null

    init {
        if (config == null) {
            config = getDefaultCompressConfig()
        }
        mCompressModule = CompressModule(config)
        startCompressTask()
    }

    private fun startCompressTask() {
        mCompressHandlerThread = HandlerThread("compressTask")
        mCompressHandlerThread?.start()
        mCompressHandler = object : Handler(mCompressHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    COMPRESS_IMAGE -> compressAction()
                }
            }
        }
    }

    fun sendCompressRequest(mediaList: MutableList<LocalMedia>) {
        localMediaList = mediaList
        if (localMediaList == null) return
        if (localMediaList?.size == 0) return
        val msg = mCompressHandler?.obtainMessage(COMPRESS_IMAGE)
        mCompressHandler?.sendMessage(msg)
    }

    private fun getDefaultCompressConfig(): CompressConfig? {
        val compressConfig = CompressConfig()
        compressConfig.grade = CompressModule.CUSTOM_GEAR
        compressConfig.maxSize = maxSizeKb * 1024 / 1000
        return compressConfig
    }

    private fun compressAction() {
        if (localMediaList == null) {
            return
        }
        if (localMediaList?.size == 0) {
            return
        }
        Loger.e(TAG, "start to compress--->image")
        try {
            for (media in localMediaList!!) {
                val oriFile = File(media.path)
                if (oriFile.length() > maxSizeKb * 1024) {
                    val file = mCompressModule?.compressImage(oriFile)
                    val path = file?.path // 压缩成功后的地址
                    // 如果是网络图片则不压缩
                    if (path != null && path.startsWith("http")) {
                        media.compressPath = ""
                    } else {
                        media.isCompressed = true
                        media.compressPath = path
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                            media.androidQToPath = path
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        onCompressResult()
    }

    private fun onCompressResult() {
        MainHandler.get().runMainThread(Runnable {
            listener?.OnCompress(localMediaList, "")
        })
    }

    fun onDestroy() {
        mCompressHandlerThread?.quit()
        mCompressHandlerThread = null

        localMediaList?.clear()
        localMediaList = null
    }


}