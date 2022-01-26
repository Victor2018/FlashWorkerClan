package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmployerAttendanceInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_employer_attendance_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerAttendanceContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerAttendanceContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: EmployerAttendanceInfo?,showConfirmStatus: Boolean) {
        itemView.mIvOnDutyConfirmStatus.setOnClickListener(this)
        itemView.mIvOffDutyConfirmStatus.setOnClickListener(this)
        itemView.mTvUserName.text = data?.username

        var onDutyTime: String? = data?.onDutyTime ?: "00:00"
        if (data?.onDutyShift == 1) {
            itemView.mTvOnDutyTime.text = onDutyTime
        } else if (data?.onDutyShift == 2) {
            itemView.mTvOnDutyTime.text = "次日" + onDutyTime
        }

        var offDutyTime: String? = data?.offDutyTime ?: "00:00"
        if (data?.offDutyShift == 1) {
            itemView.mTvOffDutyTime.text = offDutyTime
        } else if (data?.offDutyShift == 2) {
            itemView.mTvOffDutyTime.text = "次日$offDutyTime"
        }

        if (data?.onDutyConfirmStatus == 1) {
            itemView.mIvOnDutyConfirmStatus.setImageResource(R.mipmap.ic_employer_edit_attendance)
        } else if (data?.onDutyConfirmStatus == 2) {
            itemView.mIvOnDutyConfirmStatus.setImageResource(R.mipmap.ic_employer_abnormal_attendance)
        }

        if (data?.onDutyStatus == 1) {//待打卡
            itemView.mTvOnDutyNotAttending.visibility = View.VISIBLE
            itemView.mTvOnDutyStatus.visibility = View.GONE
            itemView.mIvOnDutyConfirmStatus.visibility = View.GONE
            itemView.mTvOnDutyNotAttending.text = "待打卡"
            itemView.mTvOnDutyTime.text = ""
        } else  if (data?.onDutyStatus == 2) {//未打卡
            itemView.mTvOnDutyNotAttending.visibility = View.VISIBLE
            itemView.mTvOnDutyStatus.visibility = View.GONE
            itemView.mIvOnDutyConfirmStatus.visibility = View.GONE
            itemView.mTvOnDutyNotAttending.text = "缺卡"
            itemView.mTvOnDutyTime.text = ""
        } else  if (data?.onDutyStatus == 3) {//正常
            itemView.mTvOnDutyStatus.visibility = View.VISIBLE
            if (showConfirmStatus) {
                itemView.mIvOnDutyConfirmStatus.visibility = View.VISIBLE
            } else {
                itemView.mIvOnDutyConfirmStatus.visibility = View.GONE
            }
            itemView.mTvOnDutyNotAttending.visibility = View.GONE
            itemView.mTvOnDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))
            itemView.mTvOnDutyStatus.text = "正常"
        } else  if (data?.onDutyStatus == 4) {//迟到
            itemView.mTvOnDutyStatus.visibility = View.VISIBLE
            itemView.mIvOnDutyConfirmStatus.visibility = View.VISIBLE
            itemView.mTvOnDutyNotAttending.visibility = View.GONE

            itemView.mTvOnDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOnDutyStatus.text = "迟到"
        }

        if (data?.offDutyConfirmStatus == 1) {
            itemView.mIvOffDutyConfirmStatus.setImageResource(R.mipmap.ic_employer_edit_attendance)
        } else if (data?.offDutyConfirmStatus == 2) {
            itemView.mIvOffDutyConfirmStatus.setImageResource(R.mipmap.ic_employer_abnormal_attendance)
        }

        if (data?.offDutyStatus == 1) {//待打卡
            itemView.mTvOffDutyNotAttending.visibility = View.VISIBLE
            itemView.mTvOffDutyStatus.visibility = View.GONE
            itemView.mIvOffDutyConfirmStatus.visibility = View.GONE
            itemView.mTvOffDutyNotAttending.text = "待打卡"
            itemView.mTvOffDutyTime.text = ""
        } else  if (data?.offDutyStatus == 2) {//未打卡
            itemView.mTvOffDutyNotAttending.visibility = View.VISIBLE
            itemView.mTvOffDutyStatus.visibility = View.GONE
            itemView.mIvOffDutyConfirmStatus.visibility = View.GONE
            itemView.mTvOffDutyNotAttending.text = "缺卡"
            itemView.mTvOffDutyTime.text = ""
        } else  if (data?.offDutyStatus == 3) {//正常
            itemView.mTvOffDutyStatus.visibility = View.VISIBLE
            if (showConfirmStatus) {
                itemView.mIvOffDutyConfirmStatus.visibility = View.VISIBLE
            } else {
                itemView.mIvOffDutyConfirmStatus.visibility = View.GONE
            }
            itemView.mTvOffDutyNotAttending.visibility = View.GONE
            itemView.mTvOffDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))
            itemView.mTvOffDutyStatus.text = "正常"
        } else  if (data?.offDutyStatus == 4) {//早退
            itemView.mTvOffDutyStatus.visibility = View.VISIBLE
            itemView.mIvOffDutyConfirmStatus.visibility = View.VISIBLE
            itemView.mTvOffDutyNotAttending.visibility = View.GONE

            itemView.mTvOffDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOffDutyStatus.text = "早退"
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}