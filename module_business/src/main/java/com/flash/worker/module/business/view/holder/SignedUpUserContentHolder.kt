package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo

import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_signed_up_user_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentUserContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class SignedUpUserContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentUserInfo?, isChecked: Boolean, isEnabled: Boolean) {
        itemView.mChkCheck.setOnClickListener(this)
        itemView.mTvCreditFreeze.setOnClickListener(this)
//        itemView.mIvMore.setOnClickListener(this)
        itemView.mTvContactEmployer.setOnClickListener(this)
        itemView.mTvEmploy.setOnClickListener(this)

        itemView.mChkCheck.isChecked = isChecked

        if (isChecked) {
            itemView.mChkCheck.isEnabled = true
        } else {
            itemView.mChkCheck.isEnabled = isEnabled
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,
            data?.headpic,R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
        itemView.mTvUserId.text = "(ID:${data?.talentUserId})"

        if (data?.source == 1) {
            itemView.mTvSource.text = "直接报名"
        } else if (data?.source == 2) {
            itemView.mTvSource.text = "受邀报名"
        }

        if (data?.sex == 0) {
            itemView.mTvSex.text = "女"
        } else if (data?.sex == 1) {
            itemView.mTvSex.text = "男"
        }

        itemView.mTvAge.text = "${data?.age}岁"

        if (data?.userIdentity == 1) {
            itemView.mTvIdentity.visibility = View.GONE
        } else if (data?.userIdentity == 2) {
            itemView.mTvIdentity.text = "学生"
            itemView.mTvIdentity.visibility = View.VISIBLE
        }

        itemView.mTvHeight.text = "${data?.height}cm"
        itemView.mTvWeight.text = "${data?.weight}kg"

        var height = data?.height ?: 0
        var weight = data?.weight ?: 0

        if (height > 0) {
            itemView.mTvHeight.visibility = View.VISIBLE
            itemView.line_height.visibility = View.VISIBLE
        } else {
            itemView.mTvHeight.visibility = View.GONE
            itemView.line_height.visibility = View.GONE
        }
        if (weight > 0) {
            itemView.mTvWeight.visibility = View.VISIBLE
        } else {
            itemView.mTvWeight.visibility = View.GONE
        }

        itemView.mTvSignUpTime.text = "报名时间：${data?.signupTime}"
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}