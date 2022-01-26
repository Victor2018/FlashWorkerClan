package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmploymentNumData
import kotlinx.android.synthetic.main.rv_employer_cancelled_header_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTerminateContractHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCancelledHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData (data: EmploymentNumData?) {
        itemView.mClHeaderRoot.setOnClickListener(this)
        var receivedCompensationAmount = AmountUtil.addCommaDots(data?.receivedCompensationAmount)
        itemView.mTvGetCompensation.text = "获赔${data?.receivedCompensationNum}人:${receivedCompensationAmount}元"

        var compensationAmount = AmountUtil.addCommaDots(data?.compensationAmount)
        itemView.mTvCompensation.text = "赔付${data?.compensationNum}人:${compensationAmount}元"
    }

}