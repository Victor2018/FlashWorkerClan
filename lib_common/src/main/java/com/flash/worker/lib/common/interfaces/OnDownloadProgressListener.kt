package com.flash.worker.lib.common.interfaces

import com.flash.worker.lib.common.data.ProgressInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnDownloadProgressListener
 * Author: Victor
 * Date: 2021/8/31 14:04
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnDownloadProgressListener {
    fun OnDownloadProgress(data: ProgressInfo?)
    fun OnDownloadCompleted(data: ProgressInfo?)
    fun OnError(error: String)
}