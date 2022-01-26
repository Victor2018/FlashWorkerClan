package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentSettlementOrderData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentWaitSettlementContentHolder
import com.flash.worker.module.business.view.holder.TalentWaitSettlementHeaderHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitSettlementAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitSettlementAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentSettlementOrderData, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return TalentWaitSettlementHeaderHolder (inflate(R.layout.rv_talent_wait_settlement_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentSettlementOrderData?, position: Int) {
        val headerViewHolder = viewHolder as TalentWaitSettlementHeaderHolder
        headerViewHolder.bindData()
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentWaitSettlementContentHolder (inflate(R.layout.rv_talent_wait_settlement_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentSettlementOrderData?, position: Int) {
        val contentViewHolder = viewHolder as TalentWaitSettlementContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}