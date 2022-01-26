package com.flash.worker.lib.common.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ClipboardUtil
 * Author: Victor
 * Date: 2021/2/9 11:21
 * Description: 
 * -----------------------------------------------------------------
 */
object ClipboardUtil {
    fun copy (context: Context?,copyKey: String?,copyValue: CharSequence?) {
        //获取剪贴板管理器：
        val cm = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData: ClipData = ClipData.newPlainText(copyKey, copyValue)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
    }
}