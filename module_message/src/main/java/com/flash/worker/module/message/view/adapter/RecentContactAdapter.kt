package com.flash.worker.module.message.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.RecentContactType
import com.flash.worker.module.message.view.holder.RecentContactContentHolder
import com.flash.worker.module.message.view.holder.TeamRecentContactContentHolder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.RecentContact


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecentContactAdapter
 * Author: Victor
 * Date: 2020/12/30 10:32
 * Description: 
 * -----------------------------------------------------------------
 */
class RecentContactAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<RecentContact, RecyclerView.ViewHolder>(context, listener)  {
    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: RecentContact?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            RecentContactType.TYPE_TEAM -> {
                return TeamRecentContactContentHolder(inflate(R.layout.rv_team_recent_contact_cell ,parent))
            }
            RecentContactType.TYPE_P2P -> {
                return RecentContactContentHolder(inflate(R.layout.rv_recent_contact_cell ,parent))
            }
        }
        return RecentContactContentHolder(inflate(R.layout.rv_recent_contact_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: RecentContact?, position: Int) {
        if (viewHolder is TeamRecentContactContentHolder) {
            viewHolder.bindData(data,getContentItemCount())
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is RecentContactContentHolder) {
            viewHolder.bindData(data,getContentItemCount())
            viewHolder.mOnItemClickListener = listener
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = super.getItemViewType(position)
        if (viewType == ITEM_TYPE_CONTENT) {
            val recentContact = mDatas?.get(position)
            when (recentContact?.sessionType) {
                SessionTypeEnum.Team -> {
                    viewType = RecentContactType.TYPE_TEAM
                }
                SessionTypeEnum.P2P -> {
                    viewType = RecentContactType.TYPE_P2P
                }
            }
        }
        return viewType
    }
}