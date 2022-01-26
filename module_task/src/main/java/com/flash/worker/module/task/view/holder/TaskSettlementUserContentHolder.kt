package com.flash.worker.module.task.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementConfirmDetailInfo
import kotlinx.android.synthetic.main.rv_task_settlement_user_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementUserContentHolder
 * Author: Victor
 * Date: 2021/12/1 14:34
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementUserContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TaskSettlementConfirmDetailInfo?) {
        itemView.mClSalaryTalent.setOnClickListener(this)
        itemView.mTvUserName.text = data?.username
        itemView.mTvSalary.text = AmountUtil.addCommaDots(data?.settledAmount)
        itemView.mTvServiceFeeAmount.text = AmountUtil.addCommaDots(data?.serviceFeeAmount)
        itemView.mTvTotalSettledAmount.text = AmountUtil.addCommaDots(data?.totalAmount)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}