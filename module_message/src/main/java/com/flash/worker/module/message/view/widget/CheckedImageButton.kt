package com.flash.worker.module.message.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageButton


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckedImageButton
 * Author: Victor
 * Date: 2021/4/27 11:59
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class CheckedImageButton: ImageButton {
    private var checked = false

    private var normalBkResId = 0

    private var checkedBkResId = 0

    private var normalImage: Drawable? = null

    private var checkedImage: Drawable? = null

    private var leftPadding = 0
    private var topPadding:Int = 0
    private  var rightPadding:Int = 0
    private  var bottomPadding:Int = 0

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
    }

    fun setPaddingValue(value: Int) {
        setPaddingValue(value, value, value, value)
    }

    fun setPaddingValue(left: Int, top: Int, right: Int, bottom: Int) {
        leftPadding = left
        topPadding = top
        rightPadding = right
        bottomPadding = bottom
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun setChecked(push: Boolean) {
        checked = push
        val image = if (push) checkedImage else normalImage
        image?.let { updateImage(it) }
        val background = if (push) checkedBkResId else normalBkResId
        if (background != 0) {
            updateBackground(background)
        }
    }

    fun setNormalBkResId(normalBkResId: Int) {
        this.normalBkResId = normalBkResId
        updateBackground(normalBkResId)
    }

    fun setCheckedBkResId(checkedBkResId: Int) {
        this.checkedBkResId = checkedBkResId
    }

    fun setNormalImageId(normalResId: Int) {
        normalImage = resources.getDrawable(normalResId)
        updateImage(normalImage)
    }

    fun setCheckedImageId(pushedResId: Int) {
        checkedImage = resources.getDrawable(pushedResId)
    }

    fun setNormalImage(bitmap: Bitmap?) {
        normalImage = BitmapDrawable(resources, bitmap)
        updateImage(normalImage)
    }

    fun setCheckedImage(bitmap: Bitmap?) {
        checkedImage = BitmapDrawable(resources, bitmap)
    }

    private fun updateBackground(resId: Int) {
        setBackgroundResource(resId)
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
    }

    private fun updateImage(drawable: Drawable?) {
        //  setScaleType(ScaleType.FIT_CENTER);
        setImageDrawable(drawable)
    }
}