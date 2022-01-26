package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_my_resume_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyResumeContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class MyResumeContentHolder(itemView: View) : ContentViewHolder(itemView)  {

    fun bindData (data: MyResumeInfo?) {
        itemView.mClResume.setOnClickListener(this)
        itemView.mTvResumeName.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}