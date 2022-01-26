package com.flash.worker.module.hire.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.holder.HireWorkExperienceContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireWorkExperienceAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class HireWorkExperienceAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<ResumeWorkExperienceInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: ResumeWorkExperienceInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HireWorkExperienceContentHolder(inflate(R.layout.rv_hire_work_experience_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: ResumeWorkExperienceInfo?, position: Int) {
        val contentViewHolder = viewHolder as HireWorkExperienceContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}