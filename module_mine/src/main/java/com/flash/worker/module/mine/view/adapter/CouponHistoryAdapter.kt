package com.flash.worker.module.mine.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.coremodel.data.bean.EmploymentRewardInfo
import com.flash.worker.lib.coremodel.data.bean.UserCouponInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.holder.CouponContentHolder
import com.flash.worker.module.mine.view.holder.CouponHistoryContentHolder
import com.flash.worker.module.mine.view.holder.EmployerRewardContentHolder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CouponHistoryAdapter
 * Author: Victor
 * Date: 2021/1/28 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class CouponHistoryAdapter(context: Context, listener: AdapterView.OnItemClickListener) :
        BaseRecycleAdapter<UserCouponInfo, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: UserCouponInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CouponHistoryContentHolder(inflate(R.layout.rv_coupon_history_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: UserCouponInfo?, position: Int) {
        val contentViewHolder = viewHolder as CouponHistoryContentHolder

        contentViewHolder.bindData(data)

        contentViewHolder.mOnItemClickListener = listener
    }

}