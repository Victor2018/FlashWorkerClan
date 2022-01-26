package com.flash.worker.lib.common.util

import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.widget.EditText


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EditTextUtil
 * Author: Victor
 * Date: 2022/1/14 14:01
 * Description: 
 * -----------------------------------------------------------------
 */

object EditTextUtil {

    /**
     * 设置EditText的hint字体的大小
     */
    fun setEditTextHintSize(editText: EditText, hintText: String?, size: Int) {
        val ss = SpannableString(hintText) //定义hint的值
        val ass = AbsoluteSizeSpan(size, true) //设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.hint = SpannedString(ss)
    }
}