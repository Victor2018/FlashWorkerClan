package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.WorkingHoursData
import com.flash.worker.module.business.view.holder.WorkingHoursContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkHoursAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkHoursAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<WorkingHoursData, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkingHoursData?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WorkingHoursContentHolder(inflate(R.layout.rv_working_hours_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkingHoursData?, position: Int) {
        val contentViewHolder = viewHolder as WorkingHoursContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}