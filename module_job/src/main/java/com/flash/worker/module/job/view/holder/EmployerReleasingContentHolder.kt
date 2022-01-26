package com.flash.worker.module.job.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerReleasingInfo
import com.flash.worker.module.job.R
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mFlEmployer
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mIvCompanyVerified
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvCompany
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvHireCount
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvHiredCount
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvRemuneration
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvServiceArea
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvSignUpCount
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvTitle
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvUnitPrice
import kotlinx.android.synthetic.main.rv_employer_releasing_cell.view.mTvWorkDate
import kotlinx.android.synthetic.main.rv_search_job_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleasingCommentContentHolder
 * Author: Victor
 * Date: 2021/1/15 11:11
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleasingContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerReleasingInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mTvTitle.text = data?.title
        if (data?.type == 2) {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle,R.mipmap.ic_urgent)
        } else {
            TextViewBoundsUtil.setTvDrawableRight(itemView.context,itemView.mTvTitle,0)
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        if (data?.payrollMethod == 1) {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${ data?.paidHour}小时/日)"
        } else {
            itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${data?.settlementPieceCount}单)"
        }

        itemView.mTvRemuneration.text = "${AmountUtil.addCommaDots(data?.totalAmount)}元"
        itemView.mTvHireCount.text = "雇用${data?.peopleCount}人"
        itemView.mTvSignUpCount.text = "报名${data?.signupNum}人"
        itemView.mTvHiredCount.text = "已雇${data?.realPeopleCount}人"

        if (data?.settlementMethod == 1) {//日结
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/时"
        } else if (data?.settlementMethod == 2) {//周结
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/时"
        } else if (data?.settlementMethod == 3){
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/单"
        }

        if (data?.identity == 1) {
            itemView.mTvCompany.text = "${data?.employerName}(企业)"
        } else  if (data?.identity == 2) {
            itemView.mTvCompany.text = "${data?.employerName}(商户)"
        } else  if (data?.identity == 3) {
            itemView.mTvCompany.text = "${data?.employerName}(个人)"
        }

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
            mTvAge.text = data?.ageRequirement
            itemView.mFlEmployer.addView(mTvAge)
        }

        if (data?.licenceAuth) {
            itemView.mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            itemView.mIvCompanyVerified.visibility = View.GONE
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}