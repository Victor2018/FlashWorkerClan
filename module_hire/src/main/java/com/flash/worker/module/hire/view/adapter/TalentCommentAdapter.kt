package com.flash.worker.module.hire.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentLastCommentInfo
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.holder.TalentCommentContentHolder
import com.flash.worker.module.hire.view.holder.TalentTaskCommentContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCommentAdapter
 * Author: Victor
 * Date: 2021/1/15 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCommentAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentLastCommentInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentLastCommentInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return TalentCommentContentHolder(inflate(R.layout.rv_talent_comment_cell ,parent))
            }
            TaskType.TYPE_TASK -> {
                return TalentTaskCommentContentHolder(inflate(R.layout.rv_talent_task_comment_cell ,parent))
            }
        }
        return TalentCommentContentHolder(inflate(R.layout.rv_talent_comment_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentLastCommentInfo?, position: Int) {
        if (viewHolder is TalentCommentContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is TalentTaskCommentContentHolder) {
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