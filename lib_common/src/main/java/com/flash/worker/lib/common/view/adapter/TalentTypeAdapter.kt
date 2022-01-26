package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.TalentTypeContentHolder
import com.flash.worker.lib.coremodel.data.bean.TalentTypeInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTypeAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTypeAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<TalentTypeInfo, RecyclerView.ViewHolder>(context, listener)  {

    var checkPosition: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentTypeInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentTypeContentHolder(inflate(R.layout.rv_talent_type_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentTypeInfo?, position: Int) {
        val contentViewHolder = viewHolder as TalentTypeContentHolder

        contentViewHolder.bindData(data,checkPosition)

        contentViewHolder.mOnItemClickListener = listener
    }
}