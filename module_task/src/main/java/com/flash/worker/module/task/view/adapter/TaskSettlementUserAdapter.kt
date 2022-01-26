package com.flash.worker.module.task.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementConfirmDetailInfo
import com.flash.worker.module.task.R
import com.flash.worker.module.task.view.holder.TaskSettlementUserContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementUserAdapter
 * Author: Victor
 * Date: 2021/12/1 14:33
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementUserAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<TaskSettlementConfirmDetailInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementConfirmDetailInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskSettlementUserContentHolder(inflate(R.layout.rv_task_settlement_user_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TaskSettlementConfirmDetailInfo?, position: Int) {
        val contentViewHolder = viewHolder as TaskSettlementUserContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}