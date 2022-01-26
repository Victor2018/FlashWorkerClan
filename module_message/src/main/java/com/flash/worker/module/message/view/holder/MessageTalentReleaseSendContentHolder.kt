package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.HomeTalentDetailData
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TalentReleaseAttachment
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.rv_message_talent_release_send_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageTalentReleaseSendContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageTalentReleaseSendContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mRlTalentRelease.setOnClickListener(this)
        itemView.mIvReSend.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, null, itemView.mCivAvatar,R.mipmap.ic_avatar)

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)

        val msgAttachment = data?.attachment
        if (msgAttachment is TalentReleaseAttachment) {
            var jobJson = msgAttachment.talentReleaseData ?: ""
            if (!TextUtils.isEmpty(jobJson)) {

                val homeTalentDetailData =
                    JsonUtils.parseObject(jobJson, HomeTalentDetailData::class.java)

                itemView.mTvTitle.text = homeTalentDetailData?.talentReleaseInfo?.title

                if (homeTalentDetailData?.talentReleaseInfo?.settlementMethod == 1) {//时薪
                    itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(homeTalentDetailData?.talentReleaseInfo?.price)}元/小时"
                } else if (homeTalentDetailData?.talentReleaseInfo?.settlementMethod == 2) {//整单
                    itemView.mTvUnitPrice.text = "${AmountUtil.addCommaDots(homeTalentDetailData?.talentReleaseInfo?.price)}元/单"
                }

                if (homeTalentDetailData?.userInfo?.sex == 0) {
                    itemView.mTvSex.text = "女"
                } else if (homeTalentDetailData?.userInfo?.sex == 1) {
                    itemView.mTvSex.text = "男"
                } else if (homeTalentDetailData?.userInfo?.sex == 2) {
                    itemView.mTvSex.text = "其他"
                }

                itemView.mTvAge.text = "${homeTalentDetailData?.userInfo?.age}岁"
                itemView.mTvWorkYears.text = homeTalentDetailData?.userInfo?.workYears
                itemView.mTvEducation.text = homeTalentDetailData?.resumeInfo?.highestEducation

                var isAtHome = homeTalentDetailData?.talentReleaseInfo?.isAtHome ?: false
                if (isAtHome) {
                    itemView.mTvServiceArea.text = "线上"
                } else {
                    if (TextUtils.isEmpty(homeTalentDetailData?.talentReleaseInfo?.workDistrict)) {
                        itemView.mTvServiceArea.text = homeTalentDetailData?.talentReleaseInfo?.workCity
                    } else {
                        itemView.mTvServiceArea.text = homeTalentDetailData?.talentReleaseInfo?.workDistrict?.replace(",","、")
                    }
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