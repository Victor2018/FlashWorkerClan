package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.TalentContentHolder
import com.flash.worker.lib.coremodel.data.bean.TalentInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentAdapter
 * Author: Victor
 * Date: 2020/12/22 18:20
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentInfo, RecyclerView.ViewHolder>(context, listener) {

    var checkCellId: String? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentContentHolder(inflate(R.layout.rv_talent_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentInfo?, position: Int) {
        val contentViewHolder = viewHolder as TalentContentHolder

        contentViewHolder.bindData(data,listener,checkCellId)


    }
}