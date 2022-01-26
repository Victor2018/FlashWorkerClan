package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerInfo
import com.flash.worker.lib.coremodel.data.bean.TalentCommentCenterInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentEvaluationCenterContentHolder
import com.flash.worker.module.business.view.holder.TalentTaskEvaluationCenterContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEvaluationCenterAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentEvaluationCenterAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentCommentCenterInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentCommentCenterInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return TalentEvaluationCenterContentHolder(inflate(R.layout.rv_talent_evaluation_center_cell ,parent))
            }
            TaskType.TYPE_TASK -> {
                return TalentTaskEvaluationCenterContentHolder(inflate(R.layout.rv_talent_task_evaluation_center_cell ,parent))
            }
        }
        return TalentEvaluationCenterContentHolder(inflate(R.layout.rv_talent_evaluation_center_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentCommentCenterInfo?, position: Int) {
        if (viewHolder is TalentEvaluationCenterContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is TalentTaskEvaluationCenterContentHolder) {
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