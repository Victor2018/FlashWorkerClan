package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentSettlementOrderData
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementDetailData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentTaskWaitSettlementContentHolder
import com.flash.worker.module.business.view.holder.TalentTaskWaitSettlementHeaderHolder
import com.flash.worker.module.business.view.holder.TalentWaitSettlementContentHolder
import com.flash.worker.module.business.view.holder.TalentWaitSettlementHeaderHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskWaitSettlementAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskWaitSettlementAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TaskSettlementDetailData, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return TalentTaskWaitSettlementHeaderHolder (inflate(R.layout.rv_talent_task_wait_settlement_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementDetailData?, position: Int) {
        val headerViewHolder = viewHolder as TalentTaskWaitSettlementHeaderHolder
        headerViewHolder.bindData()
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentTaskWaitSettlementContentHolder (inflate(R.layout.rv_talent_task_wait_settlement_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementDetailData?, position: Int) {
        val contentViewHolder = viewHolder as TalentTaskWaitSettlementContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}