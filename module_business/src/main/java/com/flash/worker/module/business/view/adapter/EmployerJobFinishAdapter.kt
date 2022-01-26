package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerJobFinishInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerJobFinishContentHolder
import com.flash.worker.module.business.view.holder.EmployerTaskFinishContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobFinishAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobFinishAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerJobFinishInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, EmployerJobFinishInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerJobFinishInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return EmployerJobFinishContentHolder (inflate(R.layout.rv_employer_job_finish_cell , parent))
            }
            TaskType.TYPE_TASK -> {
                return EmployerTaskFinishContentHolder (inflate(R.layout.rv_employer_task_finish_cell , parent))
            }
        }
        return EmployerJobFinishContentHolder (inflate(R.layout.rv_employer_job_finish_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerJobFinishInfo?, position: Int) {
        if (viewHolder is EmployerJobFinishContentHolder) {
            viewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is EmployerTaskFinishContentHolder) {
            viewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)
            viewHolder.mOnItemClickListener = listener
        }

    }

    fun isItemChecked (data: EmployerJobFinishInfo?): Boolean {
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