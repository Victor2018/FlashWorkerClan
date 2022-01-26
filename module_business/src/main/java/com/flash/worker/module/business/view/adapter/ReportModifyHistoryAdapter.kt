package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.DisputeHistoryInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.ReportModifyHistoryContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportModifyHistoryAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportModifyHistoryAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<DisputeHistoryInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: DisputeHistoryInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReportModifyHistoryContentHolder(inflate(R.layout.rv_report_modify_his_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: DisputeHistoryInfo?, position: Int) {
        val contentViewHolder = viewHolder as ReportModifyHistoryContentHolder
        data?.categoryPosition = position
        contentViewHolder.bindData(data,listener)
        contentViewHolder.mOnItemClickListener = listener
    }
}