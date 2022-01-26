package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerEmployingInfo

import kotlinx.android.synthetic.main.rv_employer_employing_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEmployingContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEmployingContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerEmployingInfo?) {
        itemView.mTvGroupMessage.setOnClickListener(this)
        itemView.mTvDetail.setOnClickListener(this)

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

        itemView.mTvEmploymentNum.text = "已雇${data?.realEmploymentNum}人"

        if (data?.settlementMethod == 1) {
            itemView.mTvSettlementMethod.text = "日结"
            itemView.tv_daily_salary.text = "元/日/人"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvSettlementMethod.text = "周结"
            itemView.tv_daily_salary.text = "元/周/人"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvSettlementMethod.text = "整单结"
            itemView.tv_daily_salary.text = "元/单/人"
        }

        itemView.mTvSettlementAmount.text = AmountUtil.addCommaDots(data?.settlementAmount)

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")
        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime(${data?.totalDays}天)(${data?.paidHour}小时/天)"

        itemView.mTvTotalAmount.text = "总价：${AmountUtil.addCommaDots(data?.totalAmount)}元/人"

        if (data?.shiftType == 1) {
            itemView.mTvWorkTime.text = "${data?.startTime}-${data?.endTime}"
        } else if (data?.shiftType == 2) {
            itemView.mTvWorkTime.text = "${data?.startTime}-次日${data?.endTime}"
        }

        if (TextUtils.isEmpty(data?.workCity)) {
            itemView.mTvServiceArea.text = "全国"
        } else {
            itemView.mTvServiceArea.text = data?.workDistrict
        }

        itemView.mTvTotalPrepaid.text = "${AmountUtil.addCommaDots(data?.totalPrepaidAmount)}元"
        itemView.mTvTotalSettlement.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"
        itemView.mTvCreditFreeze.text = "${AmountUtil.addCommaDots(data?.signupFrozenAmount)}元/人"

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}