package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.ViolationContentHolder
import com.flash.worker.lib.common.view.holder.ViolationHeaderHolder
import com.flash.worker.lib.coremodel.data.bean.ViolationLabelInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentViolationAdapter
 * Author: Victor
 * Date: 2021/7/8 10:18
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<ViolationLabelInfo, RecyclerView.ViewHolder>(context, listener) {

    var isReportTalentViolation: Boolean = false

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return ViolationHeaderHolder (inflate(R.layout.rv_violation_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: ViolationLabelInfo?, position: Int) {
        val headerViewHolder = viewHolder as ViolationHeaderHolder
        headerViewHolder.bindData(data,isReportTalentViolation)
        headerViewHolder.mOnItemClickListener = listener
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViolationContentHolder (inflate(R.layout.rv_violation_content_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: ViolationLabelInfo?, position: Int) {
        val contentViewHolder = viewHolder as ViolationContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }


}