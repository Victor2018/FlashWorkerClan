package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_wait_prepaid_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitPrepaidContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitPrepaidContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerSettlementOrderInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mTvJobOrderId.setOnClickListener(this)
        itemView.mIvMore.setOnClickListener(this)
        itemView.mTvContactEmployer.setOnClickListener(this)
        itemView.mTvPrepayment.setOnClickListener(this)

        itemView.mChkCheck.isChecked = isChecked

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic,R.mipmap.ic_avatar)
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

        itemView.mTvTotalPrepaid.text = "${AmountUtil.addCommaDots(data?.prepaidAmount)}元"
        itemView.mTvSettledAmount.text = "${AmountUtil.addCommaDots(data?.settledAmount)}元"
        itemView.mTvTotalSettlement.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"

        itemView.mTvJobOrderId.text = "工单号：${data?.jobOrderId}"

        var activePrepaid = data?.activePrepaid ?: false

        if (activePrepaid) {
            if (isChecked) {
                itemView.mChkCheck.isEnabled = true
            } else {
                itemView.mChkCheck.isEnabled = isEnabled
            }
        } else {
            itemView.mChkCheck.isEnabled = false
        }

        if (activePrepaid) {
            itemView.mTvPrepayment.setBackgroundResource(R.drawable.border_f7a730_radius_27)
            itemView.mTvPrepayment.setTextColor(ResUtils.getColorRes(R.color.color_F7A730))
        } else {
            itemView.mTvPrepayment.setBackgroundResource(R.drawable.border_dddddd_radius_27)
            itemView.mTvPrepayment.setTextColor(ResUtils.getColorRes(R.color.color_999999))
        }

    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}