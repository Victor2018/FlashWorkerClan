package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo
import kotlinx.android.synthetic.main.rv_settlement_date_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementDateContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementDateContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SettlementDateInfo?, settlementMethod: Int?) {
        when (settlementMethod) {
            1 -> {//日结
                itemView.mTvDate.text = data?.settlementStartTime
            }
            2 -> {//周结
                itemView.mTvDate.text = "${data?.settlementStartTime}-${data?.settlementEndTime}"
            }
            3 -> {//整单结
                itemView.mTvDate.text = "${data?.settlementStartTime}-${data?.settlementEndTime}"
            }
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}