package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.BalanceFlowContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BalanceFlowAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class BalanceFlowAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<BalanceFlowInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: BalanceFlowInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BalanceFlowContentHolder(inflate(R.layout.rv_balance_flow_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: BalanceFlowInfo?, position: Int) {
        val contentViewHolder = viewHolder as BalanceFlowContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}