package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_settled_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployeSettledContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettledContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerSettlementOrderInfo?) {
        itemView.mTvJobOrderId.setOnClickListener(this)

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic, R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.status == 1) {
            itemView.mTvStatus.text = "待到岗"
        } else if (data?.status == 2) {
            itemView.mTvStatus.text = "已到岗"
        } else if (data?.status == 3) {
            itemView.mTvStatus.text = "已预付"
        } else if (data?.status == 4) {
            itemView.mTvStatus.text = "已完工"
        } else if (data?.status == 5) {
            itemView.mTvStatus.text = "已结算"
        }

        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        if (data?.userIdentity == 1) {
            itemView.mTvIdentity.visibility = View.GONE
        } else if (data?.userIdentity == 2) {
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

        itemView.mTvRewardAmount.text = AmountUtil.addCommaDots(data?.rewardAmount) + "元"
        itemView.mTvSettled.text = AmountUtil.addCommaDots(data?.settledAmount) + "元"
        itemView.mTvTotalSettlement.text = AmountUtil.addCommaDots(data?.totalSettledAmount) + "元"
        itemView.mTvJobOrderId.text = "工单号：" + data?.jobOrderId
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}