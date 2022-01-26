package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_rejected_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentRejectedReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentRejectedReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentReleaseInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mTvDelete.setOnClickListener(this)

        if (TextUtils.isEmpty(data?.remark)) {
            itemView.mTvReason.text = "发布消息违规"
        } else {
            itemView.mTvReason.text = data?.remark
        }

        itemView.mTvTitle.text = data?.title

        if (data?.settlementMethod == 1) {
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}/小时"
        } else {
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}/单"
        }

        itemView.mFlSericeArea.removeAllViews()

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            val mTvArea = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
            mTvArea.text = "全国"
            itemView.mFlSericeArea.addView(mTvArea)
        } else {
            if (!TextUtils.isEmpty(data?.workDistrict)) {
                if (data?.workDistrict?.contains(",")!!) {
                    var areas = data?.workDistrict?.split(",")
                    for (area in areas!!) {
                        val mTvArea = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
                        mTvArea.text = area
                        itemView.mFlSericeArea.addView(mTvArea)
                    }
                } else {
                    val mTvArea = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
                    mTvArea.text = data?.workDistrict
                    itemView.mFlSericeArea.addView(mTvArea)
                }
            } else {
                val mTvCity = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
                mTvCity.text = data?.workCity
                itemView.mFlSericeArea.addView(mTvCity)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}