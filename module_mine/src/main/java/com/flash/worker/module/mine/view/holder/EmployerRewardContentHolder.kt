package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmploymentRewardInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_employer_reward_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerRewardContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerRewardContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmploymentRewardInfo?) {
        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime(${data?.totalDays}天)"

        var identity = ""
        if (data?.identity == 1) {
            identity = "企业"
        } else if (data?.identity == 2) {
            identity = "商户"
        } else if (data?.identity == 3) {
            identity = "个人"
        }
        itemView.mTvCompany.text = "${data?.employerName}($identity)"

        var licenceAuth = data?.licenceAuth ?: false
        if (licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

        if (data?.settlementMethod == 1) {
            itemView.mTvTitle.text =  "${data?.title}(日结)"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvTitle.text = "${data?.title}(周结)"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvTitle.text = "${data?.title}(整单结)"
        }

        itemView.mTvTotalSettledAmount.text = "支付:${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"

        if (data?.status == 1) {//待领取
            itemView.mIvReceive.setImageResource(R.mipmap.ic_receive_employer_reward)
        } else if (data?.status == 2) {//已领取
            itemView.mIvReceive.setImageResource(R.mipmap.ic_employer_reward_received)
        } else if (data?.status == 3) {//已过期
            itemView.mIvReceive.setImageResource(R.mipmap.ic_employer_reward_expired)
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}