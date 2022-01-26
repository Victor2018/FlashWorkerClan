package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportAppealData
import com.flash.worker.module.business.view.holder.ReportAppealSelectContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportAppealAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportAppealAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<ReportAppealData, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: ReportAppealData?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReportAppealSelectContentHolder(inflate(R.layout.rv_report_appeal_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: ReportAppealData?, position: Int) {
        val contentViewHolder = viewHolder as ReportAppealSelectContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}