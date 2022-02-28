package com.flash.worker.module.job.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SearchEmployerReleaseInfo
import com.flash.worker.module.job.R
import kotlinx.android.synthetic.main.rv_search_job_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchJobReleaseContentHolder
 * Author: Victor
 * Date: 2021/1/15 11:11
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchJobReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SearchEmployerReleaseInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mTvTitle.text = data?.title
        if (data?.type == 2) {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle,R.mipmap.ic_urgent)
        } else {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle,0)
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime ?: "","yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime ?: "","yyyy.MM.dd","MM.dd")

        itemView.mTvRemuneration.text = "${AmountUtil.addCommaDots(data?.totalAmount)}元"
        itemView.mTvHireCount.text = "雇用${data?.peopleCount}人"
        itemView.mTvSignUpCount.text = "报名${data?.signupNum}人"
        itemView.mTvHiredCount.text = "已雇${data?.realPeopleCount}人"

        var settlementPieceCount = data?.settlementPieceCount
        if (data?.settlementMethod == 1) {//日结
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/时"
        } else if (data?.settlementMethod == 2) {//周结
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/时"
        } else if (data?.settlementMethod == 3){
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/单"
            settlementPieceCount = 1
        }

        if (data?.payrollMethod == 1) {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${data?.paidHour}小时/日)"
        } else {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${settlementPieceCount}单)"
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)

        var identity = ""
        if (data?.identity == 1) {
            identity = "企业"
        } else if (data?.identity == 2) {
            identity = "商户"
        } else if (data?.identity == 3) {
            identity = "个人"
        }
        itemView.mTvCompany.text = "${data?.employerName}($identity)"

        itemView.mTvEmployer.text = data?.username

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            itemView.mTvServiceArea.text = "线上"
        } else {
            itemView.mTvServiceArea.text = data?.workDistrict
        }

        itemView.mFlEmployer.removeAllViews()
        val mTvSettlementMethod = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
        if (data?.settlementMethod == 1) {
            mTvSettlementMethod.text = "日结"
        } else if (data?.settlementMethod == 2) {
            mTvSettlementMethod.text = "周结"
        } else if (data?.settlementMethod == 3) {
            mTvSettlementMethod.text = "整单结"
        }
        itemView.mFlEmployer.addView(mTvSettlementMethod)

        if (data?.identityRequirement == 2) {
            val mTvOnlyStudent = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
            mTvOnlyStudent.text = "仅限学生"
            itemView.mFlEmployer.addView(mTvOnlyStudent)
        }

        if (data?.isAtHome!!) {
            val mTvDoAtHome = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
            mTvDoAtHome.text = "远程可做"
            itemView.mFlEmployer.addView(mTvDoAtHome)
        }

        val mTvSex = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
        if (data?.sexRequirement == 0) {
            mTvSex.text = "女"
            itemView.mFlEmployer.addView(mTvSex)
        } else if (data?.sexRequirement == 1) {
            mTvSex.text = "男"
            itemView.mFlEmployer.addView(mTvSex)
        }

        if (!TextUtils.isEmpty(data?.eduRequirement)) {
            val mTvEduRequirement = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
            mTvEduRequirement.text = data?.eduRequirement
            itemView.mFlEmployer.addView(mTvEduRequirement)
        }

        if (!TextUtils.isEmpty(data?.ageRequirement) && !TextUtils.equals("不限",data?.ageRequirement)) {
            val mTvAge = inflater?.inflate(R.layout.fl_search_job_release_cell, null) as TextView
            mTvAge.text = data?.ageRequirement + "岁"
            itemView.mFlEmployer.addView(mTvAge)
        }

        if (data?.licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

        itemView.mTvEmployerCreditScore.text = "信用分: ${data?.employerCreditScore}"

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}