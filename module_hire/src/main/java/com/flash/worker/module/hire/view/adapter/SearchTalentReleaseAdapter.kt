package com.flash.worker.module.hire.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SearchTalentReleaseInfo
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.holder.SearchTalentReleaseContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTalentReleaseAdapter
 * Author: Victor
 * Date: 2020/12/31 10:03
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTalentReleaseAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<SearchTalentReleaseInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchTalentReleaseInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchTalentReleaseContentHolder(inflate(R.layout.rv_search_talent_release_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SearchTalentReleaseInfo?, position: Int) {
        val contentViewHolder = viewHolder as SearchTalentReleaseContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}