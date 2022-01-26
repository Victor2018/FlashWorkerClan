package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.TalentJobFinishInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.TalentJobFinishContentHolder
import com.flash.worker.module.business.view.holder.TalentTaskFinishContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCancelledAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobFinishAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentJobFinishInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, TalentJobFinishInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentJobFinishInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return TalentJobFinishContentHolder (inflate(R.layout.rv_talent_job_finish_cell , parent))
            }
            TaskType.TYPE_TASK -> {
                return TalentTaskFinishContentHolder (inflate(R.layout.rv_talent_task_finish_cell , parent))
            }
        }
        return TalentJobFinishContentHolder (inflate(R.layout.rv_talent_job_finish_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentJobFinishInfo?, position: Int) {
        if (viewHolder is TalentJobFinishContentHolder) {
            viewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is TalentTaskFinishContentHolder) {
            viewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)
            viewHolder.mOnItemClickListener = listener
        }

    }

    fun isItemChecked (data: TalentJobFinishInfo?): Boolean {
        if (checkMap[data?.id] != null) {
            return true
        }
        return false
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