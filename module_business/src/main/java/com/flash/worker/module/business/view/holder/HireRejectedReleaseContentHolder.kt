package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_hire_rejected_release_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireRejectedReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class HireRejectedReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    var mEmployerReleaseInfo: EmployerReleaseInfo? = null

    fun bindData (data: EmployerReleaseInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
        if (TextUtils.isEmpty(data?.remark)) {
            itemView.mTvReason.text = "发布消息违规"
        } else {
            itemView.mTvReason.text = data?.remark
        }

        itemView.mTvDelete.setOnClickListener(this)

        mEmployerReleaseInfo = data

        var identity = ""
        if (data?.identity == 1) {
            identity = "企业"
        } else if (data?.identity == 2) {
            identity = "商户"
        } else if (data?.identity == 3) {
            identity = "个人"
        }

        if (TextUtils.isEmpty(data?.employerName)) {
            itemView.mTvCompany.text = ""
        } else {
            itemView.mTvCompany.text = "${data?.employerName}($identity)"
        }

        itemView.mTvTitle.text = data?.title

        if (TextUtils.isEmpty(data?.jobStartTime) || TextUtils.isEmpty(data?.jobEndTime)) {
            itemView.mTvReleaseDate.text = ""
        } else {
            itemView.mTvReleaseDate.text = data?.jobStartTime + "-" + data?.jobEndTime
        }

        if (data?.settlementMethod == 1) {
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/小时"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/小时"
        } else if (data?.settlementMethod == 3){
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/单"
        }

        var isAtHome = data?.isAtHome ?: false
        var workCity = data?.workCity ?: ""
        var workDistrict = data?.workDistrict ?: ""
        var address = workCity + workDistrict
        if (TextUtils.isEmpty(address) && isAtHome) {
            itemView.mTvAddress.text = "全国"
        } else {
            itemView.mTvAddress.text = workCity + workDistrict
        }

        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"
        itemView.mFlEmployer.removeAllViews()
        val mTvSettlementMethod = inflater.inflate(R.layout.fl_talent_release_cell, null) as TextView
        if (data?.settlementMethod == 1) {
            mTvSettlementMethod.text = "日结"
        } else if (data?.settlementMethod == 2) {
            mTvSettlementMethod.text = "周结"
        } else if (data?.settlementMethod == 3) {
            mTvSettlementMethod.text = "整单结"
        }

        if (!TextUtils.isEmpty(mTvSettlementMethod.text)) {
            itemView.mFlEmployer.addView(mTvSettlementMethod)
        }

        if (data?.identityRequirement == 2) {
            val mTvOnlyStudent = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
            mTvOnlyStudent.text = "仅限学生"
            itemView.mFlEmployer.addView(mTvOnlyStudent)
        }

        if (data?.isAtHome!!) {
            val mTvDoAtHome = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
            mTvDoAtHome.text = "远程可做"
            itemView.mFlEmployer.addView(mTvDoAtHome)
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}