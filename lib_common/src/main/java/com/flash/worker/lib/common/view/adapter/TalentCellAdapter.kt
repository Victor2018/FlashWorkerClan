package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.TalentCellContentHolder
import com.flash.worker.lib.coremodel.data.bean.TalentCellInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCellAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCellAdapter (context: Context, listener: AdapterView.OnItemClickListener?, var parentPosition: Int) :
        BaseRecycleAdapter<TalentCellInfo, RecyclerView.ViewHolder>(context, listener) {

    var checkCellId: String? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentCellInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TalentCellContentHolder(inflate(R.layout.rv_talent_item_cell ,parent),parentPosition)
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: TalentCellInfo?, position: Int) {
        val contentViewHolder = viewHolder as TalentCellContentHolder

        contentViewHolder.bindData(data,checkCellId)

        contentViewHolder.mOnItemClickListener = listener
    }
}