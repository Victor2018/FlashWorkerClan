package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.lib.coremodel.data.bean.MonthIncomeStatisticsInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.MemberIncomeContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberIncomeAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberIncomeAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<MonthIncomeStatisticsInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: MonthIncomeStatisticsInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberIncomeContentHolder(inflate(R.layout.rv_member_income_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: MonthIncomeStatisticsInfo?, position: Int) {
        val contentViewHolder = viewHolder as MemberIncomeContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }
}