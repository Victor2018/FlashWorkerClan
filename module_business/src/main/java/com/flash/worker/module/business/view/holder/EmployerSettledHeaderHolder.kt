package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.lib.coremodel.data.bean.SettlementNumData
import kotlinx.android.synthetic.main.rv_employer_settled_header_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettledHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettledHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData (data: SettlementNumData?) {
        itemView.mClHeaderRoot.setOnClickListener(this)
        
        itemView.mTvPrepaidCount.text = "预付${data?.prepaidNum ?: 0}人"
        itemView.mTvFinishCount.text = "完工${data?.finishNum ?: 0}人"
        itemView.mTvSettledCount.text = "结算${data?.settledNum ?: 0}人"
    }
}