package com.flash.worker.module.business.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.SettlementConfirmDetailInfo
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.holder.EmployUserContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployUserAdapter
 * Author: Victor
 * Date: 2020/12/22 18:08
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployUserAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<SettlementConfirmDetailInfo, RecyclerView.ViewHolder>(context, listener) {

    var salaryTxt: String? = null

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: SettlementConfirmDetailInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EmployUserContentHolder (inflate(R.layout.rv_employ_user_cell , parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: SettlementConfirmDetailInfo?, position: Int) {
        val contentViewHolder = viewHolder as EmployUserContentHolder

        contentViewHolder.bindData(data,salaryTxt)

        contentViewHolder.mOnItemClickListener = listener
    }

}