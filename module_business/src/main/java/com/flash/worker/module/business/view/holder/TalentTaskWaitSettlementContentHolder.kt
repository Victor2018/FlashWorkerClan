package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskSettlementDetailData
import kotlinx.android.synthetic.main.rv_talent_task_wait_settlement_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskWaitSettlementContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskWaitSettlementContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TaskSettlementDetailData?) {
        itemView.mIvMore.setOnClickListener(this)
        itemView.mTvContactEmployer.setOnClickListener(this)
        itemView.mTvRemindSettlement.setOnClickListener(this)

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

        itemView.mTvTotalPrepaidAmount.text = "已预付：${AmountUtil.addCommaDots(data?.prepaidAmount)}元"
        itemView.mTvTaskCount.text = "${data?.taskQty}件"

        var timesLimit = data?.timesLimit ?: 0
        if (timesLimit == 1) {
            itemView.mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            itemView.mTvTimesLimit.text = "一人多件"
        }

        itemView.mTvPrice.text = AmountUtil.addCommaDots(data?.price)

        var finishTimeLimitUnit = data?.finishTimeLimitUnit ?: 0
        if (finishTimeLimitUnit == 1) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}小时完成"
        } else if (finishTimeLimitUnit == 2) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}天完成"
        }

        itemView.mTvReceiveCount.text = "领取：${data?.taskReceiveQty}件"
        itemView.mTvSettlementTime.text = "${data?.settlementTimeLimit}小时内结算"

        var finishTime = DateUtil.transDate(data?.finishTime,
            "yyyy.MM.dd HH:mm:ss","yyyy.MM.dd HH:mm")
        itemView.mTvFinishDeadline.text = "提交任务时间：$finishTime"

    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}