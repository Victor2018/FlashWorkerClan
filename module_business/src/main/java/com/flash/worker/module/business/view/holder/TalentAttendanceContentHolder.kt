package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentAttendanceInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_talent_attendance_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentAttendanceContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentAttendanceContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: TalentAttendanceInfo?) {
        itemView.mTvOnDuty.setOnClickListener(this)
        itemView.mTvOffDuty.setOnClickListener(this)
        itemView.mIvOnDutyConfirmStatus.setOnClickListener(this)
        itemView.mIvOffDutyConfirmStatus.setOnClickListener(this)

        itemView.mTvAttendanceDate.text = data?.attendanceDate

        if (data?.onDutyShift == 1) {
            itemView.mTvOnDutyTime.text = data?.onDutyTime
        } else if (data?.onDutyShift == 2) {
            itemView.mTvOnDutyTime.text = "次日${data?.onDutyTime}"
        }
        if (data?.offDutyShift == 1) {
            itemView.mTvOffDutyTime.text = data?.offDutyTime
        } else if (data?.offDutyShift == 2) {
            itemView.mTvOffDutyTime.text = "次日${data?.offDutyTime}"
        }

        if (data?.onDutyStatus == 1) {//待打卡
            itemView.mTvOnDutyStatus.visibility = View.GONE
            itemView.mTvOnDuty.visibility = View.VISIBLE
        } else  if (data?.onDutyStatus == 2) {//未打卡
            itemView.mTvOnDutyStatus.visibility = View.VISIBLE
            itemView.mTvOnDuty.visibility = View.GONE

            itemView.mTvOnDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOnDutyStatus.text = "缺卡"
        } else  if (data?.onDutyStatus == 3) {//正常
            itemView.mTvOnDutyStatus.visibility = View.VISIBLE
            itemView.mTvOnDuty.visibility = View.GONE

            itemView.mTvOnDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))
            itemView.mTvOnDutyStatus.text = "正常"
        } else  if (data?.onDutyStatus == 4) {//迟到
            itemView.mTvOnDutyStatus.visibility = View.VISIBLE
            itemView.mTvOnDuty.visibility = View.GONE

            itemView.mTvOnDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOnDutyStatus.text = "迟到"
        }

        if (data?.offDutyStatus == 1) {//待打卡
            itemView.mTvOffDutyStatus.visibility = View.GONE
            itemView.mTvOffDuty.visibility = View.VISIBLE
        } else  if (data?.offDutyStatus == 2) {//未打卡
            itemView.mTvOffDutyStatus.visibility = View.VISIBLE
            itemView.mTvOffDuty.visibility = View.GONE

            itemView.mTvOffDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOffDutyStatus.text = "缺卡"
        } else  if (data?.offDutyStatus == 3) {//正常
            itemView.mTvOffDutyStatus.visibility = View.VISIBLE
            itemView.mTvOffDuty.visibility = View.GONE

            itemView.mTvOffDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))
            itemView.mTvOffDutyStatus.text = "正常"
        } else  if (data?.offDutyStatus == 4) {//早退
            itemView.mTvOffDutyStatus.visibility = View.VISIBLE
            itemView.mTvOffDuty.visibility = View.GONE

            itemView.mTvOffDutyStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvOffDutyStatus.text = "早退"
        }

        if (data?.onDutyConfirmStatus == 1) {
            itemView?.mIvOnDutyConfirmStatus.visibility = View.GONE
        } else if (data?.onDutyConfirmStatus == 2) {
            itemView?.mIvOnDutyConfirmStatus.visibility = View.VISIBLE
        }

        if (data?.offDutyConfirmStatus == 1) {
            itemView?.mIvOffDutyConfirmStatus.visibility = View.GONE
        } else if (data?.offDutyConfirmStatus == 2) {
            itemView?.mIvOffDutyConfirmStatus.visibility = View.VISIBLE
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}