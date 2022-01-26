package com.flash.worker.module.hire.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentLastCommentInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_talent_task_comment_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskCommentContentHolder
 * Author: Victor
 * Date: 2021/1/15 11:11
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTaskCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentLastCommentInfo?) {
        if (adapterPosition == 0) {
            itemView.mViewHead.visibility = View.GONE
        } else {
            itemView.mViewHead.visibility = View.VISIBLE
        }

        var anonymous = data?.anonymous ?: false
        if (anonymous) {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,R.mipmap.ic_avatar)
            itemView.mTvUserName.text = "匿名"
        } else {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic)
            itemView.mTvUserName.text = data?.username
        }

        if (data?.label == 1) {
            itemView.mTvLabel.text = "好评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvLabel,
                R.mipmap.ic_very_good_checked_small)
        } else if (data?.label == 2) {
            itemView.mTvLabel.text = "中评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvLabel,
                R.mipmap.ic_general_checked_small)
        } else if (data?.label == 3) {
            itemView.mTvLabel.text = "差评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvLabel,
                R.mipmap.ic_very_bad_checked_small)
        }
        itemView.mTvCommentTime.text = data?.commentTime
        itemView.mTvEvaluation.text = data?.content

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

        itemView.mTvTitle.text = data?.title
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}