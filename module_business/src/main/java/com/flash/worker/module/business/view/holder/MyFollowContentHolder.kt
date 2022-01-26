package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentFavReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_my_follow_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyFollowContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class MyFollowContentHolder(itemView: View) : ContentViewHolder(itemView)  {

    fun bindData (data: TalentFavReleaseInfo?) {
        itemView.mTvTitle.text = data?.title
        if (data?.type == 2) {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle, R.mipmap.ic_urgent)
        } else {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle,0)
        }

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            itemView.mTvServiceArea.text = "线上"
        } else {
            itemView.mTvServiceArea.text = data?.workDistrict
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        if (data?.payrollMethod == 1) {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${data?.paidHour}小时/日)"
        } else {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${data?.settlementPieceCount}单)"
        }

        itemView.mTvTotalAmount.text = "${AmountUtil.addCommaDots(data?.totalAmount)}元"

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

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)
        itemView.mTvEmployer.text = data?.username

        itemView.mTvEmployerCreditScore.text = "信用分: ${data?.employerCreditScore}"
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}