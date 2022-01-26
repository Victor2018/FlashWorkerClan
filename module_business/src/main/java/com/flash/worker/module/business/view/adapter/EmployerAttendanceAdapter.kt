package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerAttendanceInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerAttendanceContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerAttendanceAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerAttendanceAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerAttendanceInfo, RecyclerView.ViewHolder>(context, listener) {

    var showConfirmStatus: Boolean = true

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerAttendanceInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerAttendanceContentHolder (inflate(R.layout.rv_employer_attendance_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerAttendanceInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerAttendanceContentHolder

        contentViewHolder.bindData(data,showConfirmStatus)

        contentViewHolder.mOnItemClickListener = listener
    }

}