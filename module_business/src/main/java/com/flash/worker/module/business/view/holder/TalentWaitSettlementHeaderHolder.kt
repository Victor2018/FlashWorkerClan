package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.common.view.holder.HeaderViewHolder
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_wait_settlement_header_cell.view.*
import kotlinx.android.synthetic.main.rv_talent_wait_settlement_header_p_cell.view.*
import kotlinx.android.synthetic.main.rv_talent_wait_settlement_header_s_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitPrepaidHeaderHolder
 * Author: Victor
 * Date: 2021/3/23 11:09
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitSettlementHeaderHolder(itemView: View): HeaderViewHolder(itemView) {

    fun bindData () {
        itemView.mTvParentTitle.setOnClickListener(this)

       /* if (TextUtils.isEmpty(spannText)) {
            itemView.mTvTip.text = text
        } else {
            SpannableUtil.setSpannableColor(
                    itemView.mTvTip, ResUtils.getColorRes(R.color.color_E26853),text,spannText)
        }*/

    }

    override fun onClick(v: View?) {
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