package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.lib.coremodel.data.bean.GuildApplyRecordInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.BalanceFlowContentHolder
import com.flash.worker.module.mine.view.holder.GuildApplyRecordContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildApplyRecordAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildApplyRecordAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<GuildApplyRecordInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: GuildApplyRecordInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GuildApplyRecordContentHolder(inflate(R.layout.rv_guild_apply_record_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: GuildApplyRecordInfo?, position: Int) {
        val contentViewHolder = viewHolder as GuildApplyRecordContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}