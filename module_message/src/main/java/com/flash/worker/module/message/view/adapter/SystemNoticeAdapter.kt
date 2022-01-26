package com.flash.worker.module.message.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SystemNoticeInfo
import com.flash.worker.lib.coremodel.data.bean.TalentJobInviteInfo
import com.flash.worker.module.message.R
import com.flash.worker.module.message.view.holder.SystemNoticeContentHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeAdapter
 * Author: Victor
 * Date: 2021/5/11 17:27
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<SystemNoticeInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SystemNoticeInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SystemNoticeContentHolder(inflate(R.layout.rv_system_notice_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SystemNoticeInfo?, position: Int) {
        val contentViewHolder = viewHolder as SystemNoticeContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}