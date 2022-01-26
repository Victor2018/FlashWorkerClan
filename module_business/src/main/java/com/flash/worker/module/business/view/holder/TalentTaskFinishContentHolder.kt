package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentJobFinishInfo
import kotlinx.android.synthetic.main.rv_talent_task_finish_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskFinishContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskFinishContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentJobFinishInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mTvDelete.setOnClickListener(this)

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
        itemView.mTvTaskCount.text = "${data?.taskQty}件"

        var timesLimit = data?.timesLimit ?: 0
        if (timesLimit == 1) {
            itemView.mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            itemView.mTvTimesLimit.text = "一人多件"
        }
        itemView.mTvPrice.text = AmountUtil.addCommaDots(data?.price)
        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"
        itemView.mTvReceiveCount.text = "领取：${data?.taskReceiveQty}件"

        if (data?.status == 2) {
            itemView.view_total_bg.visibility = View.GONE
            itemView.tv_settlement_amount.visibility = View.GONE
            itemView.mTvTotalSettlementAmount.visibility = View.GONE

            if (data?.cancelSignupType == 1) {
                itemView.mTvFinishType.text = "人才取消"
            } else if (data?.cancelSignupType == 2) {
                itemView.mTvFinishType.text = "系统取消"
            } else if (data?.cancelSignupType == 3) {
                itemView.mTvFinishType.text = "雇主取消"
            }

        } else if (data?.status == 5) {
            itemView.view_total_bg.visibility = View.VISIBLE
            itemView.tv_settlement_amount.visibility = View.VISIBLE
            itemView.mTvTotalSettlementAmount.visibility = View.VISIBLE
        }

        itemView.mTvTotalSettlementAmount.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"

        if (data?.finishType == 1) {//人才解约
            itemView.tv_payment_amount?.visibility = View.VISIBLE
            itemView.mTvPaymentAmount?.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit?.visibility = View.VISIBLE
            itemView.mTvFinishType.text = "人才解约"
            itemView.tv_payment_amount?.text = "赔付："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.compensationAmount)
        } else if (data?.finishType == 2) {//雇主解约
            itemView.tv_payment_amount?.visibility = View.VISIBLE
            itemView.mTvPaymentAmount?.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit?.visibility = View.VISIBLE
            itemView.mTvFinishType.text = "雇主解约"
            itemView.tv_payment_amount?.text = "获赔："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.receivedCompensationAmount)
        } else if (data?.finishType == 3) {
            itemView.tv_payment_amount?.visibility = View.GONE
            itemView.mTvPaymentAmount?.visibility = View.GONE
            itemView.tv_payment_amount_unit?.visibility = View.GONE
            itemView.mTvFinishType.text = "任务完成"
        } else {
            itemView.tv_payment_amount?.visibility = View.GONE
            itemView.mTvPaymentAmount?.visibility = View.GONE
            itemView.tv_payment_amount_unit?.visibility = View.GONE
        }

        itemView.mChkCheck.isChecked = isChecked

        if (isChecked) {
            itemView.mChkCheck.isEnabled = true
        } else {
            itemView.mChkCheck.isEnabled = isEnabled
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}