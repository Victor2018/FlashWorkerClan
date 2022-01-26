package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerJobInviteInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_invitation_sent_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InvitationSentContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class InvitationSentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerJobInviteInfo?) {
        itemView.view_employ_bg.setOnClickListener(this)

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic, R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        } else if (data?.sex == 2) {
            itemView.mTvSex.text = "其他"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        if (data?.userIdentity == 1) {
            itemView.mTvIdentity.visibility = View.GONE
        } else if (data?.userIdentity == 2) {
            itemView.mTvIdentity.text = "学生"
            itemView.mTvIdentity.visibility = View.VISIBLE
        }


        var height = data?.height ?: 0
        var weight = data?.weight ?: 0
        if (height > 0) {
            itemView.mTvHeight.text = "${data?.height}cm"
            itemView.mTvHeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
        } else {
            itemView.mTvHeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }
        if (weight > 0) {
            itemView.mTvWeight.text = "${data?.weight}kg"
            itemView.mTvWeight.visibility = View.VISIBLE
        } else {
            itemView.mTvWeight.visibility = View.GONE
        }

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

        if (data?.settlementMethod == 1) {
            itemView.mTvTitle.text = "${data?.title}(日结)"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvTitle.text = "${data?.title}(周结)"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvTitle.text = "${data?.title}(整单结)"
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime"
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}