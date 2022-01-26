package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.GuildNewsInfo
import com.flash.worker.lib.coremodel.data.bean.WithdrawHistoryInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.GuildNewsContentHolder
import com.flash.worker.module.mine.view.holder.GuildNewsViewContentHolder
import com.flash.worker.module.mine.view.holder.WithdrawHistoryContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawHistoryAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawHistoryAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<WithdrawHistoryInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: WithdrawHistoryInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WithdrawHistoryContentHolder(inflate(R.layout.rv_withdraw_history_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: WithdrawHistoryInfo?, position: Int) {
        val contentViewHolder = viewHolder as WithdrawHistoryContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}