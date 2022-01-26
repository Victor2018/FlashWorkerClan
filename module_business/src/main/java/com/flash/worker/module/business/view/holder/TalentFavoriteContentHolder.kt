package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerFavReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_favorete_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavoriteContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavoriteContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerFavReleaseInfo?) {
        var inflater = LayoutInflater.from(itemView.context)
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

        if (data?.height!! > 0) {
            itemView.mTvHeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
            itemView.mTvHeight.text = "${data?.height}cm"
        } else {
            itemView.mTvHeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }
        if (data?.weight!! > 0) {
            itemView.mTvWeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
            itemView.mTvWeight.text = "${data?.weight}kg"
        } else {
            itemView.mTvWeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }

        itemView.mTvUserName.text = data?.username
        itemView.mTvTalentCreditScore.text = "信用分: ${data?.talentCreditScore}"
        itemView.mTvGuild.text = data?.guildName

        if (TextUtils.isEmpty(data?.guildName)) {
            itemView.mTvGuild.visibility = View.INVISIBLE
        } else {
            itemView.mTvGuild.visibility = View.VISIBLE
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

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic,R.mipmap.ic_avatar)

        itemView.mFlQualification.removeAllViews()
        if (data?.certificateNames != null && data?.certificateNames?.size!! > 0) {

            for (item in data?.certificateNames!!) {
                val mTvQualification = inflater?.inflate(R.layout.fl_search_talent_release_cell, null) as TextView
                mTvQualification.text = item
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