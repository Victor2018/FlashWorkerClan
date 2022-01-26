package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.RewardLabelContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardLabelAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardLabelAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<RewardLabelInfo, RecyclerView.ViewHolder>(context, listener)  {

    var checkMap = HashMap<String, RewardLabelInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: RewardLabelInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RewardLabelContentHolder(inflate(R.layout.rv_reward_label_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: RewardLabelInfo?, position: Int) {
        val contentViewHolder = viewHolder as RewardLabelContentHolder

        contentViewHolder.bindData(data,isItemChecked(data))

        contentViewHolder.mOnItemClickListener = listener
    }


    fun isItemChecked (data: RewardLabelInfo?): Boolean {
        if (checkMap[data?.id] != null) {
            return true
        }
        return false
    }
}