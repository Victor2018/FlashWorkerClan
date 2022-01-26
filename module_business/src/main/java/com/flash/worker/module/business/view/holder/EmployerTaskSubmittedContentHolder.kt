package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo
import com.flash.worker.module.business.R

import kotlinx.android.synthetic.main.rv_employer_task_submitted_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskSubmittedContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskSubmittedContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TaskSettledInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mIvMore.setOnClickListener(this)
        itemView.mTvContactTalent.setOnClickListener(this)
        itemView.mTvSubmitDetail.setOnClickListener(this)
        itemView.mTvSettlement.setOnClickListener(this)

        itemView.mChkCheck.isChecked = isChecked

        if (isChecked) {
            itemView.mChkCheck.isEnabled = true
        } else {
            itemView.mChkCheck.isEnabled = isEnabled
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic, R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = String.format("(ID:%s)",data?.talentUserId)


        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        itemView.mTvPrepaidAmount.text = "待结算：${AmountUtil.addCommaDots(data?.prepaidAmount)}元"

        var settlementDeadline= DateUtil.transDate(data?.settlementDeadline,
            "yyyy.MM.dd HH:mm:ss","yyyy.MM.dd HH:mm")
        itemView.mTvSettlementTime.text = "${settlementDeadline} 前结算"
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}