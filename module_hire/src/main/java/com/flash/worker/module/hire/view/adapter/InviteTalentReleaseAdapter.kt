package com.flash.worker.module.hire.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.data.TaskType
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.InviteTalentEmployerReleaseInfo
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.holder.InviteTalentJobContentHolder
import com.flash.worker.module.hire.view.holder.InviteTalentTaskContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InviteTalentReleaseAdapter
 * Author: Victor
 * Date: 2021/1/6 20:06
 * Description: 
 * -----------------------------------------------------------------
 */
class InviteTalentReleaseAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<InviteTalentEmployerReleaseInfo, RecyclerView.ViewHolder>(context, listener) {

    var checkPosition: Int = -1

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: InviteTalentEmployerReleaseInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TaskType.TYPE_JOB -> {
                return InviteTalentJobContentHolder(inflate(R.layout.rv_invite_talent_job_cell ,parent))
            }
            TaskType.TYPE_TASK -> {
                return InviteTalentTaskContentHolder(inflate(R.layout.rv_invite_talent_task_cell ,parent))
            }
        }
        return InviteTalentJobContentHolder(inflate(R.layout.rv_invite_talent_job_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: InviteTalentEmployerReleaseInfo?, position: Int) {
        if (viewHolder is InviteTalentJobContentHolder) {
            viewHolder.bindData(data,checkPosition)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is InviteTalentTaskContentHolder) {
            viewHolder.bindData(data,checkPosition)
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