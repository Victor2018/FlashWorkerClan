package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.JobWorkPicContentHolder
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobWorkPicAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class JobWorkPicAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<WorkPicInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return JobWorkPicContentHolder(inflate(R.layout.rv_job_work_pic_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
        val contentViewHolder = viewHolder as JobWorkPicContentHolder

        contentViewHolder.bindData(context,data)

        contentViewHolder.mOnItemClickListener = listener
    }
}