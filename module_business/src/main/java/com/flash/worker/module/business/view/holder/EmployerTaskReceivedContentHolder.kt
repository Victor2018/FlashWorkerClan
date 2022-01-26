package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo
import com.flash.worker.module.business.R

import kotlinx.android.synthetic.main.rv_employer_task_received_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskSettlementContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskReceivedContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TaskSettledInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mIvMore.setOnClickListener(this)
        itemView.mTvContactTalent.setOnClickListener(this)
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
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"


        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        }

        itemView.mTvAge.text = "${data?.age}岁"
        itemView.mTvReceiveCount.text = "领取${data?.taskReceiveQty}件"

        itemView.mTvPrepaidAmount.text = "已预付：${AmountUtil.addCommaDots(data?.prepaidAmount)}元"

        var finishDeadline= DateUtil.transDate(data?.finishDeadline,
            "yyyy.MM.dd HH:mm:ss","MM-dd-HH:mm")
        itemView.mTvSubmitTime.text = "提交任务截止时间：${finishDeadline}"
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}