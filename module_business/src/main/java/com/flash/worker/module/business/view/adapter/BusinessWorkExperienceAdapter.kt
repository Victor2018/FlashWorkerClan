package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.BusinessWorkExperienceContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BusinessWorkExperienceAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class BusinessWorkExperienceAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<ResumeWorkExperienceInfo, RecyclerView.ViewHolder>(context, listener)  {

    var showOnly: Boolean = false

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: ResumeWorkExperienceInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BusinessWorkExperienceContentHolder(inflate(R.layout.rv_business_work_experience_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: ResumeWorkExperienceInfo?, position: Int) {
        val contentViewHolder = viewHolder as BusinessWorkExperienceContentHolder

        contentViewHolder.bindData(context,data,showOnly)

        contentViewHolder.mOnItemClickListener = listener
    }
}