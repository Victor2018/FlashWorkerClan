package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.MonthIncomeStatisticsInfo
import kotlinx.android.synthetic.main.rv_member_income_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberIncomeContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberIncomeContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: MonthIncomeStatisticsInfo?) {
        itemView.mTvBizName.text = data?.bizName
        itemView.mTvBizDesc.text = data?.bizDesc
        itemView.mTvPaymentTime.text = data?.paymentTime
        itemView.mTvCommissionAmount.text = AmountUtil.addCommaDots(data?.commissionAmount)

        if (data?.status == 1) {//待到账
            itemView.mTvStatus.text = "待到账"
        } else  if (data?.status == 2) {//已到账
            itemView.mTvStatus.text = "已到账"
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}