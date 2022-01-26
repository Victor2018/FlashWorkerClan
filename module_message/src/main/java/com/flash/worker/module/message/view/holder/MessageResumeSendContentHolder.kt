package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentResumeDetialData
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.ResumeAttachment
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.rv_message_resume_send_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageResumeSendContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageResumeSendContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mRlResume.setOnClickListener(this)
        itemView.mIvReSend.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, null, itemView.mCivAvatar,R.mipmap.ic_avatar)

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)

        val msgAttachment = data?.attachment
        if (msgAttachment is ResumeAttachment) {
            var resumeJson = msgAttachment.resumeData ?: ""
            if (!TextUtils.isEmpty(resumeJson)) {

                val resumeDetailData =
                    JsonUtils.parseObject(resumeJson, TalentResumeDetialData::class.java)

                itemView.mTvUserName.text = resumeDetailData?.userInfo?.username

                if (resumeDetailData?.userInfo?.sex == 0) {
                    itemView.mTvSex.text = "女"
                } else if (resumeDetailData?.userInfo?.sex == 1) {
                    itemView.mTvSex.text = "男"
                } else {
                    itemView.mTvSex.text = "其他"
                }

                itemView.mTvEducation.text = resumeDetailData?.resumeInfo?.highestEducation
                itemView.mTvWorkYears.text = "${resumeDetailData?.userInfo?.workYears}工作经验"
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