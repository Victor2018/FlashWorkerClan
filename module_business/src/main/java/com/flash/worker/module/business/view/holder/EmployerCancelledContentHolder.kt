package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerCancelledInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_cancelled_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCancelledContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCancelledContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerCancelledInfo?) {
        itemView.mTvJobOrderId.setOnClickListener(this)
        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic, R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.finishType == 1) {
            itemView.mTvStatus.text = "人才解约"
        } else if (data?.finishType == 2) {
            itemView.mTvStatus.text = "雇主解约"
        } else if (data?.finishType == 3) {
            itemView.mTvStatus.text = "完工结束"
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

        if (data?.finishType == 1) {
            itemView.mTvCompensation.text = "${AmountUtil.addCommaDots(data?.compensationAmount)}元"
            itemView.tv_compensation.text = "获赔"
        } else if (data?.finishType == 2) {
            itemView.mTvCompensation.text = "${AmountUtil.addCommaDots(data?.receivedCompensationAmount)}元"
            itemView.tv_compensation.text = "赔付"
        }
        itemView.mTvTotalSettlement.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"
        itemView.mTvJobOrderId.text = "工单号：${data?.jobOrderId}"
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}