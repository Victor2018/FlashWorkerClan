package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import kotlinx.android.synthetic.main.rv_attendance_date_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceDateContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class AttendanceDateContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: String?) {
        itemView.mTvDate.text = data
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}