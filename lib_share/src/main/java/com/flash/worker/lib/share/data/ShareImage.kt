package com.flash.worker.lib.share.data

import android.graphics.Bitmap
import java.lang.ref.WeakReference


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareImage
 * Author: Victor
 * Date: 2020/11/30 19:26
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareImage() {
    private var image: WeakReference<Bitmap>? = null

    fun setImage(bitmap: Bitmap?) {
        if (bitmap == null) return
        image = WeakReference(bitmap)
    }

    fun getImage(): Bitmap? {
        return image!!.get()
    }
}