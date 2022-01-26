package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.BusinessWorkPicContentHolder
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BusinessWorkPicAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class BusinessWorkPicAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<WorkPicInfo, RecyclerView.ViewHolder>(context, listener)  {

    var showOnly: Boolean = false
    var workPicTitle: String? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BusinessWorkPicContentHolder(inflate(R.layout.rv_business_work_pic_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: WorkPicInfo?, position: Int) {
        val contentViewHolder = viewHolder as BusinessWorkPicContentHolder

        contentViewHolder.bindData(context,data,showOnly,workPicTitle)
        contentViewHolder.mOnItemClickListener = listener
    }
}