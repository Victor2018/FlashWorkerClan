package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.WorkAreaContentHolder
import com.flash.worker.lib.coremodel.data.bean.AreaInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkAreaAdapter
 * Author: Victor
 * Date: 2021/1/6 20:06
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkAreaAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<AreaInfo, RecyclerView.ViewHolder>(context, listener) {

    var checkPosition: Int = -1
    var currentCity: String? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: AreaInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WorkAreaContentHolder(inflate(R.layout.rv_work_area_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: AreaInfo?, position: Int) {
        val contentViewHolder = viewHolder as WorkAreaContentHolder

        contentViewHolder.bindData(data,checkPosition)

        contentViewHolder.mOnItemClickListener = listener
    }

}