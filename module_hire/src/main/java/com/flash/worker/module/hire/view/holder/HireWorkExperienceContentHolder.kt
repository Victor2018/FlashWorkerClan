package com.flash.worker.module.hire.view.holder

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo
import com.flash.worker.module.hire.R
import kotlinx.android.synthetic.main.rv_hire_work_experience_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireWorkExperienceContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class HireWorkExperienceContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ResumeWorkExperienceInfo?) {
        itemView.mClWorkExperience.setOnClickListener(this)
        itemView.mTvCompany.text = data?.companyName
        itemView.mTvPosition.text = data?.positionName
        itemView.mTvDate.text = data?.startTime + "-" + data?.endTime
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}