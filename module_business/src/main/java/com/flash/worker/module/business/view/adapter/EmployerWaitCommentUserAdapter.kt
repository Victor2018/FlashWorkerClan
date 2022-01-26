package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentUserInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployerWaitCommentUserContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentUserAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentUserAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<EmployerWaitCommentUserInfo, RecyclerView.ViewHolder>(context, listener) {

    var status: Int = 0

    var checkMap = HashMap<String, EmployerWaitCommentUserInfo>()

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerWaitCommentUserInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployerWaitCommentUserContentHolder (inflate(R.layout.rv_employer_wait_comment_user_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: EmployerWaitCommentUserInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployerWaitCommentUserContentHolder

        contentViewHolder.bindData(data,isItemChecked(data),checkMap.size < 5)

        contentViewHolder.mOnItemClickListener = listener
    }

    fun isItemChecked (data: EmployerWaitCommentUserInfo?): Boolean {
        if (checkMap[data?.jobOrderId] != null) {
            return true
        }
        return false
    }

}