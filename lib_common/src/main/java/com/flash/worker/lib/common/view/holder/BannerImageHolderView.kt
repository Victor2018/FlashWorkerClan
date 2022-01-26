package com.flash.worker.lib.common.view.holder

import android.view.View
import android.widget.ImageView
import com.bigkoo.convenientbanner.holder.Holder
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.widget.ShapedImageView
import com.flash.worker.lib.coremodel.data.bean.BannerInfo
import com.google.android.material.imageview.ShapeableImageView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BannerImageHolderView
 * Author: Victor
 * Date: 2021/6/19 16:00
 * Description: 
 * -----------------------------------------------------------------
 */
class BannerImageHolderView(itemView: View?) : Holder<BannerInfo>(itemView) {
    private var imageView: ShapedImageView? = null

    override fun initView(itemView: View?) {
        imageView = itemView?.findViewById(R.id.mIvBannerImage)
    }
    override fun updateUI(data: BannerInfo?) {
        ImageUtils.instance.loadImage(itemView.context,imageView,data?.imageUrl,R.mipmap.img_banner_place_holder)
    }

}