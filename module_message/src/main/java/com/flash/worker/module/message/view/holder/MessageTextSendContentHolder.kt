package com.flash.worker.module.message.view.holder

import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.view.View
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.module.message.R
import com.flash.worker.module.message.util.MoonUtil
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.rv_message_text_send_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageTextSendContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageTextSendContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mIvReSend.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, null, itemView.mCivAvatar,R.mipmap.ic_avatar)

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)

        MoonUtil.identifyFaceExpression(
            App.get(),
            itemView.mTvMessage,
            data?.content,
            ImageSpan.ALIGN_BOTTOM
        )
//        itemView.mTvMessage.movementMethod = LinkMovementMethod.getInstance()

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