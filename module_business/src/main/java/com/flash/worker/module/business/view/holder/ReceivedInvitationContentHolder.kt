package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentJobInviteInfo
import kotlinx.android.synthetic.main.rv_received_invitation_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReceivedInvitationContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ReceivedInvitationContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentJobInviteInfo?) {
        itemView.mTvContactEmployer.setOnClickListener(this)

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

        itemView.mTvTitle.text = data?.title

        itemView.mTvTotalAmount.text = AmountUtil.addCommaDots(data?.totalAmount)

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            itemView.mTvWorkArea.text = "线上"
        } else {
            itemView.mTvWorkArea.text = data?.workDistrict
        }

        if (data?.settlementMethod == 1) {
            itemView.mTvSettlementMethod.text = "日结"
            itemView.tv_daily_salary.text = "元/日"
            itemView.mTvDailySalary.text = AmountUtil.addCommaDots(data?.dailySalary)
        } else if (data?.settlementMethod == 2) {
            itemView.mTvSettlementMethod.text = "周结"
            itemView.tv_daily_salary.text = "元/日"
            itemView.mTvDailySalary.text = AmountUtil.addCommaDots(data?.dailySalary)
        } else if (data?.settlementMethod == 3) {
            itemView.mTvSettlementMethod.text = "整单结"
            itemView.tv_daily_salary.text = "元/单"
            itemView.mTvDailySalary.text = AmountUtil.addCommaDots(data?.settlementAmount)
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime(${data?.totalDays}天)(${data?.paidHour}小时/天)"

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}