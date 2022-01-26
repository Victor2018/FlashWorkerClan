package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerDisputeInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_task_dispute_handling_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskDisputeHandlingContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskDisputeHandlingContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerDisputeInfo?) {
        itemView.mTvDelete.setOnClickListener(this)
        itemView.mTvCancel.setOnClickListener(this)
        itemView.mTvHandleDetail.setOnClickListener(this)

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic,R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = String.format("(ID:%s)",data?.talentUserId)

        if (data?.disputeType == 1) {
            itemView.mTvDisputeType.text = "人才举报"
            itemView.mTvDisputeType.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvCancel.visibility = View.GONE
        } else  if (data?.disputeType == 2) {
            itemView.mTvDisputeType.text = "我的投诉"
            itemView.mTvDisputeType.setTextColor(ResUtils.getColorRes(R.color.color_3464D1))

            if (data?.status == 15 || data?.status == 30) {
                itemView.mTvCancel.visibility = View.GONE
            } else {
                itemView.mTvCancel.visibility = View.VISIBLE
            }
        }

        if (data?.status == 30) {
            itemView.mTvDelete.visibility = View.VISIBLE
        } else {
            itemView.mTvDelete.visibility = View.GONE
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
            itemView.mTvIdentity.visibility = View.VISIBLE
            itemView.mTvIdentity.text = "学生"
        }

        var identity = ""
        if (data?.identity == 1) {
            identity = "企业"
        } else if (data?.identity == 2) {
            identity = "商户"
        } else if (data?.identity == 3) {
            identity = "个人"
        }
        itemView.mTvCompany.text = "${data?.employerName}($identity)"

        var licenceAuth = data?.licenceAuth ?: false
        if (licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

        itemView.mTvTitle.text = data?.title
        itemView.mTvMessage.text = data?.message

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}