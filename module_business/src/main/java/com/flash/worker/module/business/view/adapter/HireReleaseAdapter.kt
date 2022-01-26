package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerReleaseInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.HireReleaseContentHolder
import com.flash.worker.module.business.view.holder.TaskReleaseContentHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireReleaseAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class HireReleaseAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerReleaseInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerReleaseInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return HireReleaseContentHolder (inflate(R.layout.rv_hire_release_cell , parent))
            }
            TaskType.TYPE_TASK -> {
                return TaskReleaseContentHolder (inflate(R.layout.rv_task_release_cell , parent))
            }
        }
        return HireReleaseContentHolder (inflate(R.layout.rv_hire_release_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerReleaseInfo?, position: Int) {
        if (viewHolder is HireReleaseContentHolder) {
            viewHolder.bindData(data,status)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is TaskReleaseContentHolder) {
            viewHolder.bindData(data,status)
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