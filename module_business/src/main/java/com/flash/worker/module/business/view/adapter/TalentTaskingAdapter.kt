package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentSettlementOrderData
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementDetailData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentTaskingContentHolder
import com.flash.worker.module.business.view.holder.TalentTaskingHeaderHolder
import com.flash.worker.module.business.view.holder.TalentWaitPrepaidHeaderHolder
import com.flash.worker.module.business.view.holder.TalentWaitPrepaidContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskWaitPrepaidAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */

class TalentTaskingAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TaskSettlementDetailData, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return TalentTaskingHeaderHolder (inflate(R.layout.rv_talent_tasking_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementDetailData?, position: Int) {
        val headerViewHolder = viewHolder as TalentTaskingHeaderHolder
        headerViewHolder.bindData()
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentTaskingContentHolder (inflate(R.layout.rv_talent_tasking_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementDetailData?, position: Int) {
        val contentViewHolder = viewHolder as TalentTaskingContentHolder
        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}