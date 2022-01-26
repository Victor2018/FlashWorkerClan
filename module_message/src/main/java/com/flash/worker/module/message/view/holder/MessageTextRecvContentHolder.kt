package com.flash.worker.module.message.view.holder

import android.text.style.ImageSpan
import android.view.View
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TeamNotificationUtil
import com.flash.worker.module.message.util.MoonUtil
import kotlinx.android.synthetic.main.rv_message_text_recv_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageTextRecvContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageTextRecvContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
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

        MoonUtil.identifyFaceExpression(
            App.get(),
            itemView.mTvMessage,
            data?.content,
            ImageSpan.ALIGN_BOTTOM
        )
//        itemView.mTvMessage.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}