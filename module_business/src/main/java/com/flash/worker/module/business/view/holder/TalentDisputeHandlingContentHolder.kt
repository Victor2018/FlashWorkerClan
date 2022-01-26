package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerFavReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.TalentDisputeInfo
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_dispute_handling_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeHandlingContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDisputeHandlingContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentDisputeInfo?) {
        itemView.mTvDelete.setOnClickListener(this)
        itemView.mTvCancel.setOnClickListener(this)
        itemView.mTvHandleDetail.setOnClickListener(this)

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

        if (data?.disputeType == 1) {
            itemView.mTvDisputeType.text = "我的举报"
            itemView.mTvDisputeType.setTextColor(ResUtils.getColorRes(R.color.color_3464D1))

            if (data?.status == 15 || data?.status == 30) {
                itemView.mTvCancel.visibility = View.GONE
            } else {
                itemView.mTvCancel.visibility = View.VISIBLE
            }
        } else if (data?.disputeType == 2) {
            itemView.mTvDisputeType.text = "雇主投诉"
            itemView.mTvDisputeType.setTextColor(ResUtils.getColorRes(R.color.color_E26853))

            itemView.mTvCancel.visibility = View.GONE
        }

        if (data?.settlementMethod == 1) {
            itemView.mTvTitle.text = "${data?.title}(日结)"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvTitle.text = "${data?.title}(周结)"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvTitle.text = "${data?.title}(整单结)"
        }
        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvMessage.text = data?.message
        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime"

        if (data?.status == 30) {
            itemView.mTvDelete.visibility = View.VISIBLE
        } else {
            itemView.mTvDelete.visibility = View.GONE
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}