package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_hire_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class HireReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerReleaseInfo?, status: Int) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mTvRefresh.setOnClickListener(this)
        itemView.mTvOffShelf.setOnClickListener(this)
        itemView.mTvRelease.setOnClickListener(this)
        itemView.mTvDelete.setOnClickListener(this)

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

        itemView.mTvEmployCount.text = "${data?.peopleCount}人"
        itemView.mTvEmploiedCount.text = "已雇${data?.realPeopleCount}人"
        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"

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

        itemView.mFlEmployer.removeAllViews()
        val mTvSettlementMethod = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
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

        when (status) {
            1 -> {//编辑中
                itemView.mTvEmployCount.visibility = View.GONE
                itemView.line_employ_count.visibility = View.GONE
                itemView.mTvEmploiedCount.visibility = View.GONE
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.GONE

//                itemView.mTvShare.visibility = View.GONE
//                itemView.line_share.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE

                itemView.mTvRelease.text = "发布"
            }
            2  -> {//发布中
                itemView.mTvEmployCount.visibility = View.VISIBLE
                itemView.line_employ_count.visibility = View.VISIBLE
                itemView.mTvEmploiedCount.visibility = View.VISIBLE
                itemView.mTvRefresh.visibility = View.VISIBLE
                itemView.line_refresh.visibility = View.VISIBLE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

//                itemView.mTvShare.visibility = View.VISIBLE
//                itemView.line_share.visibility = View.VISIBLE

                itemView.mTvOffShelf.visibility = View.VISIBLE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.GONE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE
            }
            3 -> {//已下架
                itemView.mTvEmployCount.visibility = View.VISIBLE
                itemView.line_employ_count.visibility = View.VISIBLE
                itemView.mTvEmploiedCount.visibility = View.VISIBLE
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

//                itemView.mTvShare.visibility = View.GONE
//                itemView.line_share.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE

                itemView.mTvRelease.text = "编辑发布"
            }
            4 -> {//已驳回
                itemView.mTvEmployCount.visibility = View.GONE
                itemView.line_employ_count.visibility = View.GONE
                itemView.mTvEmploiedCount.visibility = View.GONE
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

//                itemView.mTvShare.visibility = View.GONE
//                itemView.line_share.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE

                itemView.mTvRelease.text = "发布"
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}