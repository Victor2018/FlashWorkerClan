package com.flash.worker.module.task.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SearchEmployerReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.SearchTaskInfo
import com.flash.worker.module.task.R
import com.flash.worker.module.task.view.holder.SearchTaskContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTaskAdapter
 * Author: Victor
 * Date: 2021/12/1 14:33
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTaskAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<SearchTaskInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchTaskInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchTaskContentHolder(inflate(R.layout.rv_search_task_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchTaskInfo?, position: Int) {
        val contentViewHolder = viewHolder as SearchTaskContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}