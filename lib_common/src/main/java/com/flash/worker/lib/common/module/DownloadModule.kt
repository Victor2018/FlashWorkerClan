package com.flash.worker.lib.common.module

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.flash.worker.lib.common.data.ProgressInfo
import com.flash.worker.lib.common.interfaces.OnDownloadProgressListener
import com.flash.worker.lib.coremodel.data.bean.LatestVersionData
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadModule
 * Author: Victor
 * Date: 2021/8/31 14:09
 * Description: 
 * -----------------------------------------------------------------
 */
class DownloadModule {
    private val TAG = "DownloadModule"
    private object Holder {val instance = DownloadModule()}

    companion object {
        val  instance: DownloadModule by lazy { Holder.instance }
    }

    fun downloadFile(context: Context,data: LatestVersionData?, listener: OnDownloadProgressListener) {
        DownLoadTask(context,listener).execute(data)
    }

    fun isFileExists (data: ProgressInfo?): Boolean {
        if (data == null) return false
        if (TextUtils.isEmpty(data.filePath)) return false

        return File(data.filePath).exists()
    }
}