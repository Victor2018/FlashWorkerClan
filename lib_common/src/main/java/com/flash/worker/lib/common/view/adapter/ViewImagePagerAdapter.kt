package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.BitmapUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.widget.PinchImageView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewImagePagerAdapter
 * Author: Victor
 * Date: 2021/5/26 17:22
 * Description: 
 * -----------------------------------------------------------------
 */
class ViewImagePagerAdapter(var context: Context): PagerAdapter() {
    var datas: MutableList<String>? = null
    private var layoutInflater: LayoutInflater? = null
    var mCurrentView: View? = null
    var mOnClickListener: View.OnClickListener? = null

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        super.setPrimaryItem(container, position, obj)
        mCurrentView = obj as View
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val imageUrl = datas?.get(position)
        val view = layoutInflater?.inflate(R.layout.view_image_cell, container, false)!!
        var mIvImage = view?.findViewById<PinchImageView>(R.id.mIvImage)
        mIvImage?.setOnClickListener(mOnClickListener)
        ImageUtils.instance.loadImage(context,mIvImage,imageUrl)
        container.addView(mIvImage)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    fun getCurrentView(): Bitmap? {
        val pinchImageView = mCurrentView?.findViewById<View>(R.id.mIvImage) as PinchImageView
        val drawable: Drawable = pinchImageView.getDrawable()
        return BitmapUtil.drawableToBitmap(drawable)
    }
}