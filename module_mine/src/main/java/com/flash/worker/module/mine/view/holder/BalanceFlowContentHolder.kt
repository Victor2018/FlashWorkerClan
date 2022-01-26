package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_balance_flow_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BalanceFlowContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class BalanceFlowContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: BalanceFlowInfo?) {
        itemView.mTvBizName.text = data?.bizName
        itemView.mTvBizDesc.text = data?.bizDesc
        itemView.mTvTradeTime.text = data?.tradeTime

//        itemView.mTvBizType.text = getBizType(data?.bizType)

        if (data?.tradeType == 1) {//收入
            itemView.tv_icon.setBackgroundResource(R.drawable.round_298dd3_radius_34)
            itemView.tv_icon.text = "收"

            itemView.mTvTradeAmount.text = "+" + AmountUtil.addCommaDots(data?.tradeAmount)
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
        } else if (data?.tradeType == 2) {//支出
            itemView.tv_icon.setBackgroundResource(R.drawable.round_ebba00_radius_34)
            itemView.tv_icon.text = "支"

            itemView.mTvTradeAmount.text = "-" + AmountUtil.addCommaDots(data?.tradeAmount)
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        } else if (data?.tradeType == 3) {//充值
            itemView.tv_icon.setBackgroundResource(R.drawable.round_ef5454_radius_34)
            itemView.tv_icon.text = "充"

            itemView.mTvTradeAmount.text = "+" + AmountUtil.addCommaDots(data?.tradeAmount)
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
        } else if (data?.tradeType == 4) {//提现
            itemView.tv_icon.setBackgroundResource(R.drawable.round_3db825_radius_34)
            itemView.tv_icon.text = "提"

            itemView.mTvTradeAmount.text = "-" + AmountUtil.addCommaDots(data?.tradeAmount)
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        } else if (data?.tradeType == 5) {//其他
            itemView.tv_icon.setBackgroundResource(R.drawable.round_8f8f8f_radius_34)
            itemView.tv_icon.text = "其"

            itemView.mTvTradeAmount.text = AmountUtil.addCommaDots(data?.tradeAmount)
            itemView.mTvTradeAmount.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        }

        itemView.mTvBizDesc.text = data?.bizDesc
    }

    fun getBizType(bizType: String?): String? {
        when (bizType) {
            "RH" -> {
                return "充值"
            }
            "WD" -> {
                return "提现"
            }
            "SI" -> {
                return "报酬收入"
            }
            "SE" -> {
                return "雇用支出"
            }
            "CP" -> {
                return "赔付"
            }
            "RF" -> {
                return "报名冻结"
            }
            "RU" -> {
                return "报名解冻"
            }
            "EF" -> {
                return "雇用冻结"
            }
        }
        return ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}