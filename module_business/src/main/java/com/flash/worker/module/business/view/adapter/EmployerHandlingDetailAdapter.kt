package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.DisputeProgressInfo
import com.flash.worker.lib.coremodel.data.bean.EmployerFavReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerDisputeHandlingContentHolder
import com.flash.worker.module.business.view.holder.EmployerHandlingDetailContentHolder
import com.flash.worker.module.business.view.holder.TalentDisputeHandlingContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerHandlingDetailAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerHandlingDetailAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<DisputeProgressInfo, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: DisputeProgressInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerHandlingDetailContentHolder (inflate(R.layout.rv_employer_handling_detail_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: DisputeProgressInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerHandlingDetailContentHolder

        contentViewHolder.bindData(data,getContentItemCount())

        contentViewHolder.mOnItemClickListener = listener
    }

}