package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentUserInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_wait_comment_user_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentUserContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentUserContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerWaitCommentUserInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mTvJobOrderId.setOnClickListener(this)
        itemView.mTvComment.setOnClickListener(this)

        itemView.mChkCheck.isChecked = isChecked

        if (isChecked) {
            itemView.mChkCheck.isEnabled = true
        } else {
            itemView.mChkCheck.isEnabled = isEnabled
        }

        itemView.mTvUserName.text = data?.username
        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic, R.mipmap.ic_avatar)
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.finishType == 1) {
            itemView.mTvFinishType.text = "人才解约"

            itemView.tv_payment_amount.visibility = View.VISIBLE
            itemView.mTvPaymentAmount.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit.visibility = View.VISIBLE
            itemView.tv_payment_amount?.text = "获赔："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.compensationAmount)
        } else if (data?.finishType == 2) {
            itemView.mTvFinishType.text = "雇主解约"

            itemView.tv_payment_amount.visibility = View.VISIBLE
            itemView.mTvPaymentAmount.visibility = View.VISIBLE
            itemView.tv_payment_amount_unit.visibility = View.VISIBLE
            itemView.tv_payment_amount?.text = "赔付："
            itemView.mTvPaymentAmount?.text = AmountUtil.addCommaDots(data?.receivedCompensationAmount)
        } else if (data?.finishType == 3) {
            itemView.mTvFinishType.text = "已结算"
            itemView.tv_payment_amount.visibility = View.GONE
            itemView.mTvPaymentAmount.visibility = View.GONE
            itemView.tv_payment_amount_unit.visibility = View.GONE
        }

        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        } else if (data?.sex == 2) {
            itemView.mTvSex.text = "其它"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        if (data?.userIdentity == 1) {
            itemView.mTvIdentity.visibility = View.GONE
        } else if (data?.sex == 2) {
            itemView.mTvIdentity.text = "学生"
            itemView.mTvIdentity.visibility = View.VISIBLE
        }

        itemView.mTvHeight.text = "${data?.height}cm"
        itemView.mTvWeight.text = "${data?.weight}kg"

        var height = data?.height ?: 0
        var weight = data?.weight ?: 0
        if (height > 0) {
            itemView.mTvHeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
        } else {
            itemView.mTvHeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }
        if (weight > 0) {
            itemView.mTvWeight.visibility = View.VISIBLE
        } else {
            itemView.mTvWeight.visibility = View.GONE
        }

        itemView.mTvSettlementAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)

        var totalRewardAmount = data?.totalRewardAmount ?: 0.0
        if (totalRewardAmount > 0.0) {
            itemView.mTvRewardAmount.text = "元(奖励:${ AmountUtil.addCommaDots(data?.totalRewardAmount)}元)"
        } else {
            itemView.mTvRewardAmount.text = "元"
        }

        itemView.mTvJobOrderId.text = "工单号：${data?.jobOrderId}"
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}