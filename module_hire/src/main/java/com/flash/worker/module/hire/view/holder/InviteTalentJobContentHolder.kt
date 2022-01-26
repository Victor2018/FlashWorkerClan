package com.flash.worker.module.hire.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.InviteTalentEmployerReleaseInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_invite_talent_job_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InviteTalentReleaseContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class InviteTalentJobContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: InviteTalentEmployerReleaseInfo?, checkPosition: Int) {
        itemView.mIvCheck.setOnClickListener(this)

        itemView.mTvTitle.text = data?.title

        var jobStartTime = DateUtil.transDate(data?.jobStartTime,"yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(data?.jobEndTime,"yyyy.MM.dd","MM.dd")
        itemView.mTvWorkDate.text = "$jobStartTime-$jobEndTime"
        itemView.mTvEmploymentCount.text = "${data?.employmentNum}人 / 已雇用${data?.realEmploymentNum}人"

        if (checkPosition == adapterPosition) {
            itemView.mIvCheck.setImageResource(R.mipmap.ic_release_checked)
        } else {
            itemView.mIvCheck.setImageResource(R.mipmap.ic_release_normal)
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}