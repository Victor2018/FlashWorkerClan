package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.HomeEmployerDetailData
import com.flash.worker.lib.im.JobAttachment
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.rv_message_job_send_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageJobSendContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageJobSendContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mRlJob.setOnClickListener(this)
        itemView.mIvReSend.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, null, itemView.mCivAvatar,R.mipmap.ic_avatar)

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

                itemView.mTvTotalAmount.text = AmountUtil.addCommaDots(employerDetailData?.totalAmount) + "元"

                var isAtHome = employerDetailData?.isAtHome ?: false
                if (isAtHome) {
                    itemView.mTvServiceArea.text = "线上"
                } else {
                    itemView.mTvServiceArea.text = employerDetailData?.workDistrict
                }
            }
        }

        setMessageStatus(data)
    }

    /**
     * 设置消息发送状态
     */
    private fun setMessageStatus(message: IMMessage?) {
        when (message?.status) {
            MsgStatusEnum.fail -> {
                itemView.mPbSendLoading.visibility = View.GONE
                itemView.mIvReSend.visibility = View.VISIBLE
            }
            MsgStatusEnum.sending -> {
                itemView.mPbSendLoading.visibility = View.VISIBLE
                itemView.mIvReSend.visibility = View.GONE
            }
            else -> {
                itemView.mPbSendLoading.visibility = View.GONE
                itemView.mIvReSend.visibility = View.GONE
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}