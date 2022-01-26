package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerFinishUserInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerFinishUserContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSignUpAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFinishUserAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerFinishUserInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerFinishUserInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerFinishUserContentHolder (inflate(R.layout.rv_employer_finish_user_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerFinishUserInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerFinishUserContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}