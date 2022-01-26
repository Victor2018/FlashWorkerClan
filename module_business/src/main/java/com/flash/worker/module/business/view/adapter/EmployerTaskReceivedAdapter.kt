package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskSettlementAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskReceivedAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TaskSettledInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, TaskSettledInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return EmployerTaskReceivedHeaderHolder (inflate(R.layout.rv_employer_task_received_header_cell, parent))
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettledInfo?, position: Int) {
        val headerViewHolder = viewHolder as EmployerTaskReceivedHeaderHolder
        headerViewHolder.bindData()
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerTaskReceivedContentHolder (inflate(R.layout.rv_employer_task_received_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettledInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerTaskReceivedContentHolder

        contentViewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)

        contentViewHolder.mOnItemClickListener = listener
    }

    fun isItemChecked (data: TaskSettledInfo?): Boolean {
        if (checkMap[data?.settlementOrderId] != null) {
            return true
        }
        return false
    }

}