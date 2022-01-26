package com.flash.worker.module.hire.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.ResumeCertificateInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_hire_qualification_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireQualificationContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class HireQualificationContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ResumeCertificateInfo?) {
        itemView.mClQualification.setOnClickListener(this)
        itemView.mTvQualification.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}