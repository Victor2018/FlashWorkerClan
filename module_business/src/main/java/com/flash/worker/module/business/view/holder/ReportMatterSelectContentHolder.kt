package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportMatterData
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.rv_report_matter_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportMatterSelectContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportMatterSelectContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ReportMatterData?) {
        itemView.mIvRemoveMatter.setOnClickListener(this)
        itemView.mClMatterRoot.setOnClickListener(this)

        itemView.mTvMatter.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}