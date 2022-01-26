package com.flash.worker.module.camera.view.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.flash.worker.module.camera.R
import com.flash.worker.module.camera.cropper.CropListener


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CropImageView
 * Author: Victor
 * Date: 2020/12/18 16:43
 * Description: 
 * -----------------------------------------------------------------
 */
class CropImageView: FrameLayout {
    private var mImageView: ImageView? = null
    private var mCropOverlayView: CropOverlayView? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {

        val inflater = LayoutInflater.from(context)
        val v: View = inflater.inflate(R.layout.crop_image_view, this, true)
        mImageView = v.findViewById<View>(R.id.img_crop) as ImageView
        mCropOverlayView = v.findViewById<View>(R.id.overlay_crop) as CropOverlayView
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        mImageView?.setImageBitmap(bitmap)
        mCropOverlayView?.setBitmap(bitmap)
    }

    fun crop(listener: CropListener?, needStretch: Boolean) {
        if (listener == null) return
        mCropOverlayView?.crop(listener, needStretch)
    }
}