package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import kotlinx.android.synthetic.main.rv_talent_task_wait_comment_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskWaitCommentContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskWaitCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentWaitCommentInfo?) {
        itemView.mTvEvaluation.setOnClickListener(this)

        var identity = ""
        if (data?.identity == 1) {
            identity = "企业"
        } else if (data?.identity == 2) {
            identity = "商户"
        } else if (data?.identity == 3) {
            identity = "个人"
        }
        itemView.mTvCompany.text = "${data?.employerName}($identity)"

        if (data?.finishType == 1) {
            itemView.mTvFinishType.text = "人才解约"
        } else if (data?.finishType == 2) {
            itemView.mTvFinishType.text = "雇主解约"
        } else if (data?.finishType == 3) {
            itemView.mTvFinishType.text = "任务完成"
        }

        var licenceAuth = data?.licenceAuth ?: false
        if (licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

        itemView.mTvTitle.text = data?.title
        itemView.mTvPrice.text = AmountUtil.addCommaDots(data?.price)
        itemView.mTvTaskCount.text = "${data?.taskQty}件"

        var timesLimit = data?.timesLimit ?: 0
        if (timesLimit == 1) {
            itemView.mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            itemView.mTvTimesLimit.text = "一人多件"
        }

        itemView.mTvReceiveCount.text = "领取：${data?.taskReceiveQty}件"
        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"
        itemView.mTvSettlementAmount.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"

        if (data?.finishType == 1) {//人才解约
            itemView.tv_payment_amount?.visibility = View.VISIBLE
            itemView.mTvPaymentAmount?.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit?.visibility = View.VISIBLE
            itemView.tv_payment_amount?.text = "赔付："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.compensationAmount)
        } else if (data?.finishType == 2) {//雇主解约
            itemView.tv_payment_amount?.visibility = View.VISIBLE
            itemView.mTvPaymentAmount?.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit?.visibility = View.VISIBLE
            itemView.tv_payment_amount?.text = "获赔："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.receivedCompensationAmount)
        } else if (data?.finishType == 3) {//完工结束
            itemView.tv_payment_amount?.visibility = View.GONE
            itemView.mTvPaymentAmount?.visibility = View.GONE
            itemView.tv_payment_amount_unit?.visibility = View.GONE
        } else {
            itemView.tv_payment_amount?.visibility = View.GONE
            itemView.mTvPaymentAmount?.visibility = View.GONE
            itemView.tv_payment_amount_unit?.visibility = View.GONE
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}