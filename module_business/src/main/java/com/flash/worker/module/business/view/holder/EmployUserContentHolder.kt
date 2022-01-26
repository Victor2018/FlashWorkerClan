package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SettlementConfirmDetailInfo
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo

import kotlinx.android.synthetic.main.rv_employ_user_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployUserContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployUserContentHolder(itemView: View): ContentViewHolder(itemView)  {

    fun bindData (data: SettlementConfirmDetailInfo?,salaryTxt: String?) {
        itemView.mClSalaryTalent.setOnClickListener(this)
        itemView.mTvUserName.text = data?.username
        itemView.mTvSalary.text = salaryTxt
        itemView.mTvCreditFreezeAmount.text = AmountUtil.addCommaDots(data?.creditFrozenAmount)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}