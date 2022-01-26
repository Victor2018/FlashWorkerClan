package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo

import kotlinx.android.synthetic.main.rv_employer_task_settled_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskSettledContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskSettledContentHolder(itemView: View): ContentViewHolder(itemView)  {

    fun bindData (data: TaskSettledInfo?) {
        itemView.mTvSubmitDetail.setOnClickListener(this)

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        itemView.mTvSettledAmount.text = "已结算：${AmountUtil.addCommaDots(data?.settledAmount)}元"
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}