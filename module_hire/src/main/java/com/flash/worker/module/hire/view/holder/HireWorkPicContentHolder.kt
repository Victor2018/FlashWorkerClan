package com.flash.worker.module.hire.view.holder

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_hire_work_pic_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireWorkPicContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class HireWorkPicContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (context: Context, data: WorkPicInfo?) {
        itemView.mIvWorkPic.setOnClickListener(this)
        ImageUtils.instance.loadImage(context,itemView.mIvWorkPic,data?.pic)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}