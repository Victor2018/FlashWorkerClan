package com.flash.worker.lib.common.view.widget

import android.os.Parcel
import android.text.TextPaint
import android.text.style.URLSpan


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: URLSpanNoUnderline
 * Author: Victor
 * Date: 2021/6/29 15:30
 * Description: 
 * -----------------------------------------------------------------
 */
class URLSpanNoUnderline(url: String) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}