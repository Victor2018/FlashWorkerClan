package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentSettlementOrderData
import kotlinx.android.synthetic.main.rv_talent_settled_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentSettledContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentSettledContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentSettlementOrderData?) {
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

        if (data?.settlementMethod == 1) {
            itemView.mTvSettlementMethod.text = "日结"
            itemView.tv_settlement_amount_unit.text = "元/日"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvSettlementMethod.text = "周结"
            itemView.tv_settlement_amount_unit.text = "元/周"

        } else if (data?.settlementMethod == 3) {
            itemView.mTvSettlementMethod.text = "整单结"
            itemView.tv_settlement_amount_unit.text = "元/单"
        }
        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime(${data?.totalDays}天)" +
                "(${data?.paidHour}小时/天)"

        if (data?.shiftType == 1) {
            itemView.mTvWorkTime.text = "${data?.startTime}-${data?.endTime}"
        } else if (data?.shiftType == 2) {
            itemView.mTvWorkTime.text = "${data?.startTime}-次日${data?.endTime}"
        }

        itemView.mTvSettlementAmount.text = AmountUtil.addCommaDots(data?.settlementAmount)
        itemView.mTvTotalAmount.text = AmountUtil.addCommaDots(data?.totalAmount)
        itemView.mTvRewardAmount.text = AmountUtil.addCommaDots(data?.rewardAmount)

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            itemView.mTvWorkArea.text = "线上"
        } else {
            itemView.mTvWorkArea.text = data?.workDistrict
        }

        itemView.mTvPrepaid.text = "${AmountUtil.addCommaDots(data?.prepaidAmount)}元"
        itemView.mTvSettled.text = "${AmountUtil.addCommaDots(data?.settledAmount)}元"
        itemView.mTvTotalSettlement.text = "${AmountUtil.addCommaDots(data?.totalSettledAmount)}元"

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}