package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.HomeEmployerDetailData
import com.flash.worker.lib.im.JobAttachment
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TeamNotificationUtil
import kotlinx.android.synthetic.main.rv_message_job_recv_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageJobRecvContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageJobRecvContentHolder(itemView: View) : ContentViewHolder(itemView){

    fun bindData (data: IMMessage?) {
        itemView.mRlJob.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        var team = TeamNotificationUtil.getTeam(data?.sessionId)
        if (team != null) {
            itemView.mTvNickName.visibility = View.VISIBLE
        } else {
            itemView.mTvNickName.visibility = View.GONE
        }

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, itemView.mTvNickName, itemView.mCivAvatar,R.mipmap.ic_avatar)

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)

        val msgAttachment = data?.attachment
        if (msgAttachment is JobAttachment) {
            var jobJson = msgAttachment.jobData ?: ""
            if (!TextUtils.isEmpty(jobJson)) {
                val employerDetailData =
                    JsonUtils.parseObject(jobJson, HomeEmployerDetailData::class.java)

                itemView.mTvTitle.text = employerDetailData?.title

                var jobStartTime = DateUtil.transDate(employerDetailData?.jobStartTime ?: "","yyyy.MM.dd","MM.dd")
                var jobEndTime = DateUtil.transDate(employerDetailData?.jobEndTime ?: "","yyyy.MM.dd","MM.dd")
                if (employerDetailData?.payrollMethod == 1) {
                    itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${employerDetailData?.paidHour}小时/日)"
                } else {
                    itemView.mTvWorkDate.text = "工作日期：${jobStartTime}-${jobEndTime}(${employerDetailData?.settlementPieceCount}单)"
                }

                itemView.mTvTotalAmount.text = "${AmountUtil.addCommaDots(employerDetailData?.totalAmount)}元"

                var isAtHome = employerDetailData?.isAtHome ?: false
                if (isAtHome) {
                    itemView.mTvServiceArea.text = "线上"
                } else {
                    itemView.mTvServiceArea.text = employerDetailData?.workDistrict
                }
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}