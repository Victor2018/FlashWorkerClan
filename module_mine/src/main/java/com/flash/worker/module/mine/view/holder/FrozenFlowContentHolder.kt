package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.FrozenFlowInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_frozen_flow_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FrozenFlowContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class FrozenFlowContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: FrozenFlowInfo?) {
        itemView.mTvBizName.text = data?.bizName
        itemView.mTvBizDesc.text = data?.bizDesc
        itemView.mTvTradeTime.text = data?.tradeTime
        itemView.mTvTradeAmount.text = AmountUtil.addCommaDots(data?.tradeAmount)

        if (data?.tradeType == 6) {//解冻
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_027AFF))
        } else if (data?.tradeType == 7) {
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}