package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TalentResumeDetialData
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.ResumeAttachment
import com.flash.worker.lib.im.TeamNotificationUtil
import kotlinx.android.synthetic.main.rv_message_resume_recv_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageResumeRecvContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageResumeRecvContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mRlResume.setOnClickListener(this)
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
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}