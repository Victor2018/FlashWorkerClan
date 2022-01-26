package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentInfo
import kotlinx.android.synthetic.main.rv_employer_wait_comment_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerWaitCommentInfo?) {
        itemView.mTvCheckInRecord.setOnClickListener(this)
        itemView.mTvEvaluationAll.setOnClickListener(this)
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

        if (data?.settlementMethod == 1) {
            itemView.mTvTitle.text = data?.title + "(日结)"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvTitle.text = data?.title + "(周结)"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvTitle.text = data?.title + "(整单结)"
        }

        if (data?.finishType == 1) {
            itemView.mTvFinishType.text = "雇主关闭"
        } else if (data?.finishType == 2) {
            itemView.mTvFinishType.text = "雇用完成"
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime"
        itemView.mTvSettlementAmount.text = AmountUtil.addCommaDots(data?.settlementAmount)
        itemView.mTvTotalSettlementAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)
        itemView.mTvCompensationAmount.text = AmountUtil.addCommaDots(data?.totalReceivedCompensationAmount)
        itemView.mTvPaymentAmount.text = AmountUtil.addCommaDots(data?.totalCompensationAmount)

        if (data?.settlementMethod == 1) {
            itemView.tv_daily_salary.text = "元/日/人"
        } else if (data?.settlementMethod == 2) {
            itemView.tv_daily_salary.text = "元/周/人"
        } else if (data?.settlementMethod == 3) {
            itemView.tv_daily_salary.text = "元/单/人"
        }

        if (TextUtils.isEmpty(data?.workCity)) {
            itemView.mTvServiceArea.text = "全国"
        } else {
            itemView.mTvServiceArea.text = data?.workDistrict
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}