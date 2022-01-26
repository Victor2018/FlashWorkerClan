package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.lib.coremodel.data.bean.SettlementNumData
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_wait_prepaid_header_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_wait_prepaid_header_p_cell.view.*
import kotlinx.android.synthetic.main.rv_talent_wait_prepaid_header_s_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitPrepaidHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitPrepaidHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData (data: SettlementNumData?, isChecked: Boolean) {
        itemView.mChkOnlyFinish.setOnClickListener(this)
        itemView.mTvParentTitle.setOnClickListener(this)
        itemView.mClHeaderRoot.setOnClickListener(this)

        itemView.mChkOnlyFinish.isChecked = isChecked
        itemView.mTvEmploiedCount.text = "雇用${data?.realEmploymentNum ?: 0}人"
        itemView.mTvArrived.text = "到岗${data?.arrivedNum ?: 0}人"
        itemView.mTvPrepaidCount.text = "预付${data?.prepaidNum ?: 0}人"
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.mTvParentTitle -> {
                if (itemView.mElWaitPrepaid.isExpanded) {
                    itemView.mElWaitPrepaid.collapse()
                } else {
                    itemView.mElWaitPrepaid.expand()
                }
            }
        }
    }

}