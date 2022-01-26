package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerCommentCenterInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_task_evaluation_center_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskEvaluationCenterContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerTaskEvaluationCenterContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: EmployerCommentCenterInfo?) {
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

        itemView.mTvTitle.text = data?.title

        if (data?.talentComment?.label == 1) {
            itemView.mTvTalentLabel.text = "好评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvTalentLabel,
                R.mipmap.ic_very_good_checked_small)
        } else if (data?.talentComment?.label == 2) {
            itemView.mTvTalentLabel.text = "中评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvTalentLabel,
                R.mipmap.ic_general_checked_small)
        } else if (data?.talentComment?.label == 3) {
            itemView.mTvTalentLabel.text = "差评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvTalentLabel,
                R.mipmap.ic_very_bad_checked_small)
        }

        var anonymous = data?.talentComment?.anonymous ?: false
        if (anonymous) {
            itemView.mTvTalentUserName.text = "匿名"
            itemView.mTvTalentUserId.text = ""
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivTalentAvatar,R.mipmap.ic_avatar)
        } else {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivTalentAvatar,
                data?.talentComment?.headpic,R.mipmap.ic_avatar)
            itemView.mTvTalentUserName.text = data?.talentComment?.username
            itemView.mTvTalentUserId.text = "(ID:${data?.talentComment?.userId})"
        }

        itemView.mTvTalentCommentTime.text = data?.talentComment?.commentTime
        itemView.mTvTalentEvaluation.text = data?.talentComment?.content

        if (data?.talentComment == null) {
            itemView.iv_talent_dot.visibility = View.GONE
            itemView.line_talent_dot.visibility = View.GONE
            itemView.tv_talent_evaluation.visibility = View.GONE
            itemView.mTvTalentLabel.visibility = View.GONE
            itemView.mCivTalentAvatar.visibility = View.GONE
            itemView.mTvTalentUserName.visibility = View.GONE
            itemView.mTvTalentUserId.visibility = View.GONE
            itemView.mTvTalentCommentTime.visibility = View.GONE
            itemView.mTvTalentEvaluation.visibility = View.GONE

            itemView.tv_my_evaluation.text = "对 ${data?.employerComment?.talentUsername}" +
                    "(ID:${data?.employerComment?.talentUserId}) 的评价"
        } else {
            itemView.iv_talent_dot.visibility = View.VISIBLE
            itemView.line_talent_dot.visibility = View.VISIBLE
            itemView.tv_talent_evaluation.visibility = View.VISIBLE
            itemView.mTvTalentLabel.visibility = View.VISIBLE
            itemView.mCivTalentAvatar.visibility = View.VISIBLE
            itemView.mTvTalentUserName.visibility = View.VISIBLE
            itemView.mTvTalentUserId.visibility = View.VISIBLE
            itemView.mTvTalentCommentTime.visibility = View.VISIBLE
            itemView.mTvTalentEvaluation.visibility = View.VISIBLE

            itemView.tv_my_evaluation.text = "我的评价"
        }


        if (data?.employerComment?.label == 1) {
            itemView.mTvMyLabel.text = "好评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_very_good_checked_small)
        } else if (data?.employerComment?.label == 2) {
            itemView.mTvMyLabel.text = "中评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_general_checked_small)
        } else if (data?.employerComment?.label == 3) {
            itemView.mTvMyLabel.text = "差评"
            TextViewBoundsUtil.setTvDrawableLeft(itemView.context,itemView.mTvMyLabel,
                R.mipmap.ic_very_bad_checked_small)
        }

        itemView.mTvMyCommentTime.text = data?.employerComment?.commentTime
        itemView.mTvMyEvaluation.text = data?.employerComment?.content


        if (data?.employerComment == null) {
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