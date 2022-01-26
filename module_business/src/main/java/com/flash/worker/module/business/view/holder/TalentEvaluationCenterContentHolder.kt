package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentCommentCenterInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_evaluation_center_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEvaluationCenterContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentEvaluationCenterContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentCommentCenterInfo?) {
        itemView.mIvMore.setOnClickListener(this)

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
            itemView.mTvTitle.text = data?.title + "(日结)"
        } else if (data?.settlementMethod == 2) {
            itemView.mTvTitle.text = data?.title + "(周结)"
        } else if (data?.settlementMethod == 3) {
            itemView.mTvTitle.text = data?.title + "(整单结)"
        }

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")

        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime"

        if (data?.employerComment?.label == 1) {
            itemView.mTvEmployerLabel.text = "好评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvEmployerLabel,
                R.mipmap.ic_very_good_checked_small)
        } else if (data?.employerComment?.label == 2) {
            itemView.mTvEmployerLabel.text = "中评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvEmployerLabel,
                R.mipmap.ic_general_checked_small)
        } else if (data?.employerComment?.label == 3) {
            itemView.mTvEmployerLabel.text = "差评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvEmployerLabel,
                R.mipmap.ic_very_bad_checked_small)
        }


        itemView.mTvEmployerCommentTime.text = data?.employerComment?.commentTime
        itemView.mTvEmployerEvaluation.text = data?.employerComment?.content


        var anonymous = data?.employerComment?.anonymous ?: false
        if (anonymous) {
            itemView.mTvEmployerUserName.text = "匿名"
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivEmployerAvatar,R.mipmap.ic_avatar)
        } else {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivEmployerAvatar,
                data?.employerComment?.headpic,R.mipmap.ic_avatar)
            itemView.mTvEmployerUserName.text = data?.employerComment?.username
        }

        if (data?.employerComment == null) {
            itemView.iv_employer_dot.visibility = View.GONE
            itemView.line_dot.visibility = View.GONE
            itemView.tv_employer_evaluation.visibility = View.GONE
            itemView.mTvEmployerLabel.visibility = View.GONE
            itemView.mCivEmployerAvatar.visibility = View.GONE
            itemView.mTvEmployerUserName.visibility = View.GONE
            itemView.mTvEmployerCommentTime.visibility = View.GONE
            itemView.mTvEmployerEvaluation.visibility = View.GONE
        } else {
            itemView.iv_employer_dot.visibility = View.VISIBLE
            itemView.line_dot.visibility = View.VISIBLE
            itemView.tv_employer_evaluation.visibility = View.VISIBLE
            itemView.mTvEmployerLabel.visibility = View.VISIBLE
            itemView.mCivEmployerAvatar.visibility = View.VISIBLE
            itemView.mTvEmployerUserName.visibility = View.VISIBLE
            itemView.mTvEmployerCommentTime.visibility = View.VISIBLE
            itemView.mTvEmployerEvaluation.visibility = View.VISIBLE
        }

        if (data?.talentComment?.label == 1) {
            itemView.mTvMyLabel.text = "好评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_very_good_checked_small)
        } else if (data?.talentComment?.label == 2) {
            itemView.mTvMyLabel.text = "中评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_general_checked_small)
        } else if (data?.talentComment?.label == 3) {
            itemView.mTvMyLabel.text = "差评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_very_bad_checked_small)
        }

        itemView.mTvMyCommentTime.text = data?.talentComment?.commentTime
        itemView.mTvMyEvaluation.text = data?.talentComment?.content

        if (data?.talentComment == null) {
            itemView.iv_my_dot.visibility = View.GONE
            itemView.line_my_dot.visibility = View.GONE
            itemView.tv_my_evaluation.visibility = View.GONE
            itemView.mTvMyLabel.visibility = View.GONE
            itemView.mTvMyCommentTime.visibility = View.GONE
            itemView.mTvMyEvaluation.visibility = View.GONE
            itemView.mIvMore.visibility = View.GONE
        } else {
            itemView.iv_my_dot.visibility = View.VISIBLE
            itemView.line_my_dot.visibility = View.VISIBLE
            itemView.tv_my_evaluation.visibility = View.VISIBLE
            itemView.mTvMyLabel.visibility = View.VISIBLE
            itemView.mTvMyCommentTime.visibility = View.VISIBLE
            itemView.mTvMyEvaluation.visibility = View.VISIBLE
            itemView.mIvMore.visibility = View.VISIBLE
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}