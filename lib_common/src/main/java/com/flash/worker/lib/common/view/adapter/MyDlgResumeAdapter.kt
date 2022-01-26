package com.flash.worker.lib.common.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.holder.MyDlgResumeContentHolder
import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyDlgResumeAdapter
 * Author: Victor
 * Date: 2020/12/19 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class MyDlgResumeAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<MyResumeInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: MyResumeInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyDlgResumeContentHolder(inflate(R.layout.rv_dlg_my_resume_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: MyResumeInfo?, position: Int) {
        val contentViewHolder = viewHolder as MyDlgResumeContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}