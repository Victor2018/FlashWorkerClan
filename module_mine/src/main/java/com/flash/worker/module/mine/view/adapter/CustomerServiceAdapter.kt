package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.CustomerServiceInfo
import com.flash.worker.lib.coremodel.data.bean.GuildMemberInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.CustomerServiceContentHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomerServiceAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class CustomerServiceAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<CustomerServiceInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: CustomerServiceInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CustomerServiceContentHolder(inflate(R.layout.rv_customer_service_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: CustomerServiceInfo?, position: Int) {
        val contentViewHolder = viewHolder as CustomerServiceContentHolder

        contentViewHolder.bindData(data,getContentItemCount())

        contentViewHolder.mOnItemClickListener = listener
    }
}