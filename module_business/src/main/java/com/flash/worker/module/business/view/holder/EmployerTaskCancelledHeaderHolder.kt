package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmploymentNumData
import kotlinx.android.synthetic.main.rv_employer_task_cancelled_header_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskCancelledHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskCancelledHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData (data: EmploymentNumData?) {
        itemView.mClHeaderRoot.setOnClickListener(this)

        var receivedCompensationAmount = AmountUtil.addCommaDots(data?.receivedCompensationAmount)
        itemView.mTvGetCompensation.text = "获赔：${receivedCompensationAmount}元"
    }

}