package com.flash.worker.lib.common.view.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.Constant
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.fragment_view_image_cell.*
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewImageFragment
 * Author: Victor
 * Date: 2022/1/14 16:47
 * Description: 
 * -----------------------------------------------------------------
 */

class ViewImageFragment: BaseFragment(),View.OnClickListener {
    companion object {

        fun newInstance(imgUrl: String?): ViewImageFragment {
            return newInstance(imgUrl,0)
        }
        fun newInstance(imgUrl: String?,id: Int): ViewImageFragment {
            val fragment = ViewImageFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            bundle.putString(Constant.INTENT_DATA_KEY, imgUrl)
            fragment.arguments = bundle
            return fragment
        }
    }

    var imageUrl: String? = null

    override fun getLayoutResource() = R.layout.fragment_view_image_cell

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize() {
        mIvImage.setOnClickListener(this)
    }

    fun initData() {
        imageUrl = arguments?.getString(Constant.INTENT_DATA_KEY)
        loadLargeImage(context!!,imageUrl,mIvImage)
    }

    override fun handleBackEvent() = false

    override fun freshFragData() {
    }

    fun loadLargeImage(context: Context, res: String?, imageView: SubsamplingScaleImageView?) {
        imageView?.isQuickScaleEnabled = true
        imageView?.maxScale = 15F
        imageView?.isZoomEnabled = true
        imageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)

        Glide.with(context).load(res).downloadOnly(object : SimpleTarget<File>(){
            override fun onResourceReady(
                resource: File,
                transition: com.bumptech.glide.request.transition.Transition<in File>?
            ) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(resource.absolutePath, options)
                val sWidth = options.outWidth
                val sHeight = options.outHeight
                options.inJustDecodeBounds = false
                val wm = ContextCompat.getSystemService(context, WindowManager::class.java)
                val width = wm?.defaultDisplay?.width ?: 0
                val height = wm?.defaultDisplay?.height ?: 0
                if (sHeight >= height
                    && sHeight / sWidth >= 3) {
                    imageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                    imageView?.setImage(ImageSource.uri(Uri.fromFile(resource)), ImageViewState(0.5f, PointF(0f, 0f), 0))
                } else {
                    imageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                    imageView?.setImage(ImageSource.uri(Uri.fromFile(resource)))
                    imageView?.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE)
                }
            }

        })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvImage -> {
                activity?.onBackPressed()
            }
        }
    }
}