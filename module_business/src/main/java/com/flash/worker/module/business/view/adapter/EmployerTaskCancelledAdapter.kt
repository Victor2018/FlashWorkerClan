package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerCancelledInfo
import com.flash.worker.lib.coremodel.data.bean.EmploymentNumData
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerCancelledContentHolder
import com.flash.worker.module.business.view.holder.EmployerCancelledHeaderHolder
import com.flash.worker.module.business.view.holder.EmployerTaskCancelledContentHolder
import com.flash.worker.module.business.view.holder.EmployerTaskCancelledHeaderHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskCancelledAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskCancelledAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerCancelledInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var headerData: EmploymentNumData? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return EmployerTaskCancelledHeaderHolder (inflate(R.layout.rv_employer_task_cancelled_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerCancelledInfo?, position: Int) {
        val headerViewHolder = viewHolder as EmployerTaskCancelledHeaderHolder
        headerViewHolder.bindData(headerData)
        headerViewHolder.mOnItemClickListener = listener
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerTaskCancelledContentHolder (inflate(R.layout.rv_employer_task_cancelled_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerCancelledInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerTaskCancelledContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}