package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildNewsInfo
import com.flash.worker.lib.coremodel.data.bean.WithdrawHistoryInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_withdraw_history_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawHistoryContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawHistoryContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: WithdrawHistoryInfo?) {
        itemView.mIvStatus.setOnClickListener(this)
        itemView.mTvTime.text = data?.tradeTime
        itemView.mTvAmount.text = AmountUtil.addCommaDots(data?.tradeAmount)

        when (data?.tradeStatus) {
            1 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_666666))
                itemView.mTvStatus.text = "审核中"
                itemView.mIvStatus.visibility = View.GONE
            }
            2 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_3DB825))
                itemView.mTvStatus.text = "审核通过"
                itemView.mIvStatus.visibility = View.GONE
            }
            3 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
                itemView.mTvStatus.text = "审核不通过"
                itemView.mIvStatus.visibility = View.VISIBLE
            }
            4 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_666666))
                itemView.mTvStatus.text = "提现请求中"
                itemView.mIvStatus.visibility = View.GONE
            }
            5 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))
                itemView.mTvStatus.text = "提现成功"
                itemView.mIvStatus.visibility = View.GONE
            }
            6 -> {
                itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
                itemView.mTvStatus.text = "提现失败"
                itemView.mIvStatus.visibility = View.VISIBLE
            }
        }

        if (adapterPosition == 0) {
            itemView.line_top.visibility = View.GONE
        } else {
            itemView.line_top.visibility = View.VISIBLE
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}