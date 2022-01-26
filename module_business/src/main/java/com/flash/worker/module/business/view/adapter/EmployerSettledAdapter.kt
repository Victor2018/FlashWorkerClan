package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.SettlementNumData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerSettledContentHolder
import com.flash.worker.module.business.view.holder.EmployerSettledHeaderHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettledAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettledAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerSettlementOrderInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var headerData: SettlementNumData? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return EmployerSettledHeaderHolder (inflate(R.layout.rv_employer_settled_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerSettlementOrderInfo?, position: Int) {
        val headerViewHolder = viewHolder as EmployerSettledHeaderHolder
        headerViewHolder.bindData(headerData)
        headerViewHolder.mOnItemClickListener = listener
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerSettledContentHolder (inflate(R.layout.rv_employer_settled_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerSettlementOrderInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerSettledContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}