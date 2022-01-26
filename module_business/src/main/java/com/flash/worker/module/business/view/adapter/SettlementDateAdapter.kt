package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.SettlementDateContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementDateAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementDateAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<SettlementDateInfo, RecyclerView.ViewHolder>(context, listener)  {

    var settlementMethod: Int? = 0//结算方式（1-日结；2-周结；3-整单结）

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SettlementDateInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SettlementDateContentHolder(inflate(R.layout.rv_settlement_date_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SettlementDateInfo?, position: Int) {
        val contentViewHolder = viewHolder as SettlementDateContentHolder

        contentViewHolder.bindData(data,settlementMethod)

        contentViewHolder.mOnItemClickListener = listener
    }
}