package com.flash.worker.module.business.view.holder

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_business_work_experience_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BusinessWorkExperienceContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class BusinessWorkExperienceContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (context: Context?, data: ResumeWorkExperienceInfo?, showOnly: Boolean) {
        itemView.mTvCompany.text = data?.companyName
        itemView.mTvPosition.text = data?.positionName
        itemView.mTvDate.text = "${data?.startTime}-${data?.endTime}"

        if (showOnly) {
            TextViewBoundsUtil.setTvDrawableRight(context!!,itemView.mTvDate,0)
        } else {
            TextViewBoundsUtil.setTvDrawableRight(context!!,itemView.mTvDate,
                R.mipmap.ic_work_experience_right)
        }

        itemView.mClWorkExperience.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}