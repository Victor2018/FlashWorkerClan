package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentInfo
import kotlinx.android.synthetic.main.rv_employer_task_wait_comment_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskWaitCommentContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskWaitCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerWaitCommentInfo?) {
        itemView.mTvEvaluationAll.setOnClickListener(this)
        itemView.mTvDetail.setOnClickListener(this)

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

        if (data?.finishType == 1) {
            itemView.mTvFinishType.text = "雇主关闭"
        } else if (data?.finishType == 2) {
            itemView.mTvFinishType.text = "任务完成"
        }

        itemView.mTvTaskCount.text = "${data?.taskQty}件"

        var timesLimit = data?.timesLimit ?: 0
        if (timesLimit == 1) {
            itemView.mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            itemView.mTvTimesLimit.text = "一人多件"
        }

        itemView.mTvPrice.text = AmountUtil.addCommaDots(data?.price)
        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"

        itemView.mTvTotalSettlementAmount.text = AmountUtil.addCommaDots(data?.totalSettledAmount)
        itemView.mTvCompensationAmount.text = AmountUtil.addCommaDots(data?.totalReceivedCompensationAmount)
        itemView.mTvPaymentAmount.text = AmountUtil.addCommaDots(data?.totalCompensationAmount)

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}