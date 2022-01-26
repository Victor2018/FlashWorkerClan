package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentJobInviteInfo
import kotlinx.android.synthetic.main.rv_task_received_invitation_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskReceivedInvitationContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskReceivedInvitationContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentJobInviteInfo?) {
        itemView.mTvContactEmployer.setOnClickListener(this)

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

        var timesLimit = data?.timesLimit ?: 0

        if (timesLimit == 1) {
            itemView.mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            itemView.mTvTimesLimit.text = "一人多件"
        }
        itemView.mTvSettlementTime.text = "${data?.settlementTimeLimit}小时内结算"

        itemView.mTvPrice.text = AmountUtil.addCommaDots(data?.price)

        var finishTimeLimitUnit = data?.finishTimeLimitUnit ?: 0
        if (finishTimeLimitUnit == 1) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}小时内完成"
        } else if (finishTimeLimitUnit == 2) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}天内完成"
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}