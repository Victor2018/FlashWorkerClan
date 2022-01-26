package com.flash.worker.lib.share.interfaces

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IShareListener
 * Author: Victor
 * Date: 2020/11/30 19:21
 * Description: 
 * -----------------------------------------------------------------
 */
interface IShareListener {
    fun getAttachActivity(): AppCompatActivity?

    fun onShareSuccess(shareType: Int)

    fun onShareCancel(shareType: Int, error: String?)

    fun onShareError(shareType: Int, error: String?)
}