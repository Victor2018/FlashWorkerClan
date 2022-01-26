package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentReleaseInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_release_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentReleaseInfo?, status: Int) {
        var inflater = LayoutInflater.from(itemView.context)
        itemView.mTvRefresh.setOnClickListener(this)
        itemView.mTvOffShelf.setOnClickListener(this)
        itemView.mTvRelease.setOnClickListener(this)
        itemView.mTvDelete.setOnClickListener(this)

        itemView.mTvReleaseDate.text = "${data?.releaseTime}-${data?.expireTime}"
        itemView.mTvInviteCount.text = "收到邀请:${data?.inviteCount}份"
        itemView.mTvTitle.text = data?.title

        if (data?.settlementMethod == 1) {
            itemView.mTvInvitePrice.text = "${AmountUtil.addCommaDots(data?.price)}/小时"
        } else {
            itemView.mTvInvitePrice.text = "${AmountUtil.addCommaDots(data?.price)}/单"
        }

        itemView.mFlSericeArea.removeAllViews()

        var isAtHome = data?.isAtHome ?: false


        if (isAtHome) {
            val mTvCity = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
            mTvCity.text = "全国"
            itemView.mFlSericeArea.addView(mTvCity)
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
                if (!TextUtils.isEmpty(data?.workCity)) {
                    val mTvCity = inflater?.inflate(R.layout.fl_talent_release_cell, null) as TextView
                    mTvCity.text = data?.workCity
                    itemView.mFlSericeArea.addView(mTvCity)
                }
            }
        }

        when (status) {
            1 -> {//编辑中
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE

                itemView.mTvReleaseDate.visibility = View.GONE
                itemView.mTvInviteCount.visibility = View.GONE

            }
            2  -> {//发布中
                itemView.mTvRefresh.visibility = View.VISIBLE
                itemView.line_refresh.visibility = View.VISIBLE

                itemView.mTvOffShelf.visibility = View.VISIBLE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.GONE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE

                itemView.mTvReleaseDate.visibility = View.VISIBLE
                itemView.mTvInviteCount.visibility = View.VISIBLE
            }
            3 -> {//已下架
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.GONE
                itemView.mTvDelete.visibility = View.GONE

                itemView.mTvReleaseDate.visibility = View.VISIBLE
                itemView.mTvInviteCount.visibility = View.VISIBLE

                itemView.mTvRelease.text = "编辑发布"
            }
            4 -> {//已驳回
                itemView.mTvRefresh.visibility = View.GONE
                itemView.line_refresh.visibility = View.GONE

                itemView.mTvOffShelf.visibility = View.GONE
                itemView.line_off_shelf.visibility = View.GONE

                itemView.mTvRelease.visibility = View.VISIBLE
                itemView.line_release.visibility = View.VISIBLE
                itemView.mTvDelete.visibility = View.VISIBLE

                itemView.mTvReleaseDate.visibility = View.GONE
                itemView.mTvInviteCount.visibility = View.GONE

                itemView.mTvRelease.text = "编辑发布"
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}