package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerFavReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.TalentDisputeInfo
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentDisputeHandlingContentHolder
import com.flash.worker.module.business.view.holder.TalentTaskDisputeHandlingContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeHandlingAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDisputeHandlingAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentDisputeInfo, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentDisputeInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return TalentDisputeHandlingContentHolder(
                    inflate(R.layout.rv_talent_dispute_handling_cell, parent))
            }
            TaskType.TYPE_TASK -> {
                return TalentTaskDisputeHandlingContentHolder(
                    inflate(R.layout.rv_talent_task_dispute_handling_cell, parent))
            }
        }
        return TalentDisputeHandlingContentHolder (inflate(R.layout.rv_talent_dispute_handling_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentDisputeInfo?, position: Int) {
        if (viewHolder is TalentDisputeHandlingContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is TalentTaskDisputeHandlingContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        }

    }

    override fun getItemViewType(position: Int): Int {
        var viewType = super.getItemViewType(position)
        if (viewType == ITEM_TYPE_CONTENT) {
            val data = mDatas?.get(position)
            when (data?.taskType) {
                1 -> {
                    viewType = TaskType.TYPE_JOB
                }
                2 -> {
                    viewType = TaskType.TYPE_TASK
                }
            }
        }
        return viewType
    }

}