package com.flash.worker.module.job.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SearchEmployerReleaseInfo
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.holder.SearchJobReleaseContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchJobReleaseAdapter
 * Author: Victor
 * Date: 2021/1/15 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchJobReleaseAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<SearchEmployerReleaseInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchEmployerReleaseInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchJobReleaseContentHolder(inflate(R.layout.rv_search_job_release_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchEmployerReleaseInfo?, position: Int) {
        val contentViewHolder = viewHolder as SearchJobReleaseContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}