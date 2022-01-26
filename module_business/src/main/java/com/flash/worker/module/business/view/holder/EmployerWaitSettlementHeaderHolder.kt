package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.lib.coremodel.data.bean.SettlementNumData
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_settled_header_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_cell.view.mClHeaderRoot
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_cell.view.mTvFinishCount
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_cell.view.mTvPrepaidCount
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_cell.view.mTvSettledCount
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_p_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_header_s_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitSettlementHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitSettlementHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData (data: SettlementNumData?, isChecked: Boolean) {
        itemView.mChkOnlyFinish.setOnClickListener(this)
        itemView.mTvParentTitle.setOnClickListener(this)
        itemView.mClHeaderRoot.setOnClickListener(this)

        itemView.mChkOnlyFinish.isChecked = isChecked
        itemView.mTvPrepaidCount.text = "预付${data?.prepaidNum ?: 0}人"
        itemView.mTvFinishCount.text = "完工${data?.finishNum ?: 0}人"
        itemView.mTvSettledCount.text = "结算${data?.settledNum ?: 0}人"
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.mTvParentTitle -> {
                if (itemView.mElWaitSettlement.isExpanded) {
                    itemView.mElWaitSettlement.collapse()
                } else {
                    itemView.mElWaitSettlement.expand()
                }
            }
        }
    }
}