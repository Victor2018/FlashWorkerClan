package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.business.data.WorkingHoursData
import kotlinx.android.synthetic.main.rv_working_hours_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkingHoursContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkingHoursContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: WorkingHoursData?) {
        itemView.mTvStartTime.text = data?.startTime
        itemView.mTvEndTime.text = data?.endTime
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}