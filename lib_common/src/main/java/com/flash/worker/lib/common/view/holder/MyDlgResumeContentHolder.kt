package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo
import kotlinx.android.synthetic.main.rv_dlg_my_resume_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyDlgResumeContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class MyDlgResumeContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: MyResumeInfo?) {
        itemView.mTvResumeName.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}