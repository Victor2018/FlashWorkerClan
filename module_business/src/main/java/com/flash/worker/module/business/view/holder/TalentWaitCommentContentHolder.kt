package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import kotlinx.android.synthetic.main.rv_talent_wait_comment_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitCommentContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

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
            itemView.mTvFinishType.text = "完成工作"
        }

        var licenceAuth = data?.licenceAuth ?: false
        if (licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

        itemView.mTvTitle.text = data?.title


        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = String.format("%s-%s",jobStartTime,jobEndTime)

        itemView.mTvTotalAmount.text = AmountUtil.addCommaDots(data?.totalAmount)
        itemView.mTvSettlementAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)

        var totalRewardAmount = data?.totalRewardAmount ?: 0.0
        if (totalRewardAmount > 0) {
            itemView.mTvRewardAmount.text = "元(奖励:${AmountUtil.addCommaDots(totalRewardAmount)}元)"
        } else {
            itemView.mTvRewardAmount.text = "元"
        }

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