package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentWaitEmployInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentWaitEmployContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentToBeEmployAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitEmployAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentWaitEmployInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentWaitEmployInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentWaitEmployContentHolder (inflate(R.layout.rv_talent_wait_employ_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentWaitEmployInfo?, position: Int) {
        val contentViewHolder = viewHolder as TalentWaitEmployContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}