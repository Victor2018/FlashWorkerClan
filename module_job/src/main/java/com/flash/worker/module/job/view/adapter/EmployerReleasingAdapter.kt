package com.flash.worker.module.job.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerReleasingInfo
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.holder.EmployerReleasingContentHolder
import com.flash.worker.module.job.view.holder.TaskReleasingContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleasingCommentAdapter
 * Author: Victor
 * Date: 2021/1/15 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleasingAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerReleasingInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerReleasingInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return EmployerReleasingContentHolder(inflate(R.layout.rv_employer_releasing_cell ,parent))
            }
            TaskType.TYPE_TASK -> {
                return TaskReleasingContentHolder(inflate(R.layout.rv_task_releasing_cell ,parent))
            }
        }
        return EmployerReleasingContentHolder(inflate(R.layout.rv_employer_releasing_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerReleasingInfo?, position: Int) {
        if (viewHolder is EmployerReleasingContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        }
        if (viewHolder is TaskReleasingContentHolder) {
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