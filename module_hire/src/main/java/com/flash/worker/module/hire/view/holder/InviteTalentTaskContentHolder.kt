package com.flash.worker.module.hire.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.InviteTalentEmployerReleaseInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_invite_talent_task_cell.view.*

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
class InviteTalentTaskContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: InviteTalentEmployerReleaseInfo?, checkPosition: Int) {
        itemView.mIvCheck.setOnClickListener(this)

        itemView.mTvTitle.text = data?.title

        itemView.mTvEmploymentCount.text = "${data?.taskQty}件 / 已领${data?.taskReceiveQty}件"

        var finishTimeLimitUnit = data?.finishTimeLimitUnit ?: 0
        if (finishTimeLimitUnit == 1) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}小时完成"
        } else if (finishTimeLimitUnit == 2) {
            itemView.mTvFinishTimeLimit.text = "限${data?.finishTimeLimit}天完成"
        }

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