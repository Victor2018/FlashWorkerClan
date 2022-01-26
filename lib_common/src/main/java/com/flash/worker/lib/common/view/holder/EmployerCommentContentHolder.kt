package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.coremodel.data.bean.EmployerLastCommentInfo
import kotlinx.android.synthetic.main.rv_employer_comment_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCommentContentHolder
 * Author: Victor
 * Date: 2021/1/15 11:11
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCommentContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerLastCommentInfo?) {
        if (adapterPosition == 0) {
            itemView.mViewHead.visibility = View.GONE
        } else {
            itemView.mViewHead.visibility = View.VISIBLE
        }

        var anonymous = data?.anonymous ?: false
        if (anonymous) {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar, R.mipmap.ic_avatar)
            itemView.mTvUserName.text = "匿名"
        } else {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic)
            itemView.mTvUserName.text = data?.username
        }

        var drawableResId = 0
        if (data?.label == 1) {
            itemView.mTvLabel.text = "好评"
            drawableResId = R.mipmap.ic_very_good_checked_small
        } else if (data?.label == 2) {
            itemView.mTvLabel.text = "中评"
            drawableResId = R.mipmap.ic_general_checked_small
        } else if (data?.label == 3) {
            itemView.mTvLabel.text = "差评"
            drawableResId = R.mipmap.ic_very_bad_checked_small
        }
        TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvLabel, drawableResId)

        itemView.mTvCommentTime.text = data?.commentTime
        itemView.mTvEvaluation.text = data?.content
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}