package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SalaryTalentInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_salary_talent_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SalaryTalentContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class SalaryTalentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SalaryTalentInfo?) {
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