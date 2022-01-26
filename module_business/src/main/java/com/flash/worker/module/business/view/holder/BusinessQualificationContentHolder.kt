package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.ResumeCertificateInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.fl_qualification_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BusinessQualificationContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class BusinessQualificationContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: ResumeCertificateInfo?) {
        itemView.mTvQualification.text = data?.name

        itemView.mClQualification.setOnClickListener(this)
        itemView.mIvQualificationDel.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}