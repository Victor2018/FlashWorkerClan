package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.EmployerCommentContentHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerLastCommentInfo

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCommentAdapter
 * Author: Victor
 * Date: 2021/1/15 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCommentAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerLastCommentInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerLastCommentInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerCommentContentHolder(inflate(R.layout.rv_employer_comment_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerLastCommentInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerCommentContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}