package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerCancelledInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_task_cancelled_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskCancelledContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskCancelledContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerCancelledInfo?) {
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

        if (data?.finishType == 1) {
            itemView.mTvCompensation.text = "获赔：${AmountUtil.addCommaDots(data?.compensationAmount)}元"
        } else if (data?.finishType == 2) {
            itemView.mTvCompensation.text = "赔付：${AmountUtil.addCommaDots(data?.receivedCompensationAmount)}元"
        }

        itemView.mTvCancelTime.text = "解约时间：${data?.finishTime}"
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}