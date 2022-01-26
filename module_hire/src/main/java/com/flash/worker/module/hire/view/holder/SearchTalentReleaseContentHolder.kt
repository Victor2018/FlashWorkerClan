package com.flash.worker.module.hire.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SearchTalentReleaseInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_search_talent_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTalentReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/31 10:04
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTalentReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SearchTalentReleaseInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mClTalentReleaseCell.setOnClickListener(this)
        itemView.mTvTalentGuild.setOnClickListener(this)

        itemView.mTvTitle.text = data?.title
        if (data?.settlementMethod == 1) {//时薪
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/小时"
        } else if (data?.settlementMethod == 2) {//整单
            itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(data?.price)}元/单"
        }

        if (data?.sex == 0) {
            itemView.mTvSex.setText("女")
        } else if (data?.sex == 1) {
            itemView.mTvSex.setText("男")
        } else if (data?.sex == 2) {
            itemView.mTvSex.setText("其他")
        }
        itemView.mTvAge.text = "${data?.age}岁"
        itemView.mTvWorkYears.text = data?.workYears
        itemView.mTvEducation.text = data?.highestEducation

        var weight = data?.weight ?: 0
        var height = data?.height ?: 0
        if (height > 0) {
            itemView.mTvHeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
            itemView.mTvHeight.text = "${data?.height}cm"
        } else {
            itemView.mTvHeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }
        if (weight > 0) {
            itemView.mTvWeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
            itemView.mTvWeight.text = "${data?.weight}kg"
        } else {
            itemView.mTvWeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }

        itemView.mTvUserName.text = data?.username
        itemView.mTvTalentCreditScore.text = "信用分: ${data?.talentCreditScore}"

        itemView.mTvTalentGuild.text = data?.guildName
        if (TextUtils.isEmpty(data?.guildName)) {
            itemView.mTvTalentGuild.visibility = View.INVISIBLE
        } else {
            itemView.mTvTalentGuild.visibility = View.VISIBLE
        }

        var isAtHome = data?.isAtHome ?: false
        if (isAtHome) {
            itemView.mTvServiceArea.text = "线上"
        } else {
            if (TextUtils.isEmpty(data?.workDistrict)) {
                itemView.mTvServiceArea.text = data?.workCity
            } else {
                itemView.mTvServiceArea.text = data?.workDistrict?.replace(",","、")
            }
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)

        itemView.mFlQualification.removeAllViews()
        var count = data?.certificateNames?.size ?: 0
        if (data?.certificateNames != null && count > 0) {
            data?.certificateNames?.forEach {
                val mTvQualification = inflater?.inflate(R.layout.fl_search_talent_release_cell, null) as TextView
                mTvQualification.text = it
                itemView.mFlQualification.addView(mTvQualification)
            }
        } else {
            //为了站位
            val mTvQualification = inflater?.inflate(R.layout.fl_search_talent_release_cell, null) as TextView
            mTvQualification.visibility = View.INVISIBLE
            itemView.mFlQualification.addView(mTvQualification)
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}