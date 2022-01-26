package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.SettlementNumData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerWaitPrepaidContentHolder
import com.flash.worker.module.business.view.holder.EmployerWaitPrepaidHeaderHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitPrepaidAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitPrepaidAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerSettlementOrderInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, EmployerSettlementOrderInfo>()

    var headerData: SettlementNumData? = null
    var showOnlyFinish: Boolean = false

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return EmployerWaitPrepaidHeaderHolder (inflate(R.layout.rv_employer_wait_prepaid_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerSettlementOrderInfo?, position: Int) {
        val headerViewHolder = viewHolder as EmployerWaitPrepaidHeaderHolder
        headerViewHolder.bindData(headerData,showOnlyFinish)
        headerViewHolder.mOnItemClickListener = listener
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerWaitPrepaidContentHolder (inflate(R.layout.rv_employer_wait_prepaid_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerSettlementOrderInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerWaitPrepaidContentHolder

        contentViewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)

        contentViewHolder.mOnItemClickListener = listener
    }

    fun isItemChecked (data: EmployerSettlementOrderInfo?): Boolean {
        if (checkMap[data?.settlementOrderId] != null) {
            return true
        }
        return false
    }

}