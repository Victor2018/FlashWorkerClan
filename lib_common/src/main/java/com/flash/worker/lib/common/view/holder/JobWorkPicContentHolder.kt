package com.flash.worker.lib.common.view.holder

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import kotlinx.android.synthetic.main.rv_job_work_pic_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobWorkPicContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class JobWorkPicContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (context: Context, data: WorkPicInfo?) {
        itemView.mIvJobPic.setOnClickListener(this)

        ImageUtils.instance.loadImage(context,itemView.mIvJobPic,data?.pic)

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}