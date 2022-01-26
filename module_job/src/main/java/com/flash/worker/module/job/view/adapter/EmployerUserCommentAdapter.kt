package com.flash.worker.module.job.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerUserCommentInfo
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.holder.EmployerUserCommentContentHolder
import com.flash.worker.module.job.view.holder.EmployerUserTaskCommentContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerUserCommentAdapter
 * Author: Victor
 * Date: 2021/1/15 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerUserCommentAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerUserCommentInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerUserCommentInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return EmployerUserCommentContentHolder(inflate(R.layout.rv_employer_user_comment_cell ,parent))
            }
            TaskType.TYPE_TASK -> {
                return EmployerUserTaskCommentContentHolder(inflate(R.layout.rv_employer_user_task_comment_cell ,parent))
            }
        }
        return EmployerUserCommentContentHolder(inflate(R.layout.rv_employer_user_comment_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerUserCommentInfo?, position: Int) {
        if (viewHolder is EmployerUserCommentContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is EmployerUserTaskCommentContentHolder) {
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