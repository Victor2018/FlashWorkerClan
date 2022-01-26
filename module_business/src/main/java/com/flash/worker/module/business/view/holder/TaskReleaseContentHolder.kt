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
import kotlinx.android.synthetic.main.rv_task_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerReleaseInfo?, status: Int) {
        itemView.mTvRefresh.setOnClickListener(this)
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
        itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/件"
        itemView.mTvTaskCount.text = "${data?.taskQty}件"

        var finishTimeLimitUnit = data?.finishTimeLimitUnit ?: 0
        if (finishTimeLimitUnit == 1) {
            itemView.mTvFinishTime.text = "限${data?.finishTimeLimit}小时完成"
        } else if (finishTimeLimitUnit == 2) {
            itemView.mTvFinishTime.text = "限${data?.finishTimeLimit}天完成"
        }

        itemView.mTvReleaseTime.text = "发布时间：${data?.releaseTime}"
        itemView.mTvSettlementTime.text = "${data?.settlementTimeLimit}小时内结算"

        when (status) {
            1 -> {//编辑中
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE
            }
            2  -> {//发布中
                itemView.mTvRefresh.visibility = View.VISIBLE
                itemView.line_refresh.visibility = View.VISIBLE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

                itemView.mTvRelease.visibility = View.GONE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE
            }
            3 -> {//已下架
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE
            }
            4 -> {//已驳回
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE
                itemView.mTvReleaseTime.visibility = View.VISIBLE

                itemView.mTvRelease.visibility = View.GONE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}