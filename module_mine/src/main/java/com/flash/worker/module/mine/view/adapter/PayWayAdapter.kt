package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.data.PayWayInfo
import com.flash.worker.module.mine.view.holder.PayWayContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayWayAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class PayWayAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<PayWayInfo, RecyclerView.ViewHolder>(context, listener)  {

    var checkPosition: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: PayWayInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PayWayContentHolder(inflate(R.layout.rv_pay_way_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: PayWayInfo?, position: Int) {
        val contentViewHolder = viewHolder as PayWayContentHolder

        contentViewHolder.bindData(data,checkPosition)

        contentViewHolder.mOnItemClickListener = listener
    }
}