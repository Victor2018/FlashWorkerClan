package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.business.data.ReportAppealData
import com.flash.worker.module.business.data.ReportMatterData
import kotlinx.android.synthetic.main.rv_report_appeal_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportAppealSelectContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportAppealSelectContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ReportAppealData?) {
        itemView.mIvRemoveAppeal.setOnClickListener(this)
        itemView.mClAppealRoot.setOnClickListener(this)

        itemView.mTvAppeal.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}