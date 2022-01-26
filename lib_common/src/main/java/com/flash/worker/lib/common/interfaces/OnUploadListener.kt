package com.flash.worker.lib.common.interfaces


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnUploadListener
 * Author: Victor
 * Date: 2020/12/9 10:18
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnUploadListener {
    fun OnUpload(progress: Int,url: String?,complete: Boolean)
}