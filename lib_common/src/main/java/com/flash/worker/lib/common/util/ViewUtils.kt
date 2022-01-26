package com.flash.worker.lib.common.util

import android.view.View


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewUtils
 * Author: Victor
 * Date: 2020/11/27 15:02
 * Description: 
 * -----------------------------------------------------------------
 */
object ViewUtils {
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }
}