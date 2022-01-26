package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.im.TeamNotificationUtil
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment
import kotlinx.android.synthetic.main.rv_message_notify_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageNotifyContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageNotifyContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        var notify: String? = data?.content ?: "对方给你发送了一条消息无法识别"
        if (data?.attachment is NotificationAttachment) {
            var teamNotificationText = TeamNotificationUtil.getTeamNotificationText(data)
            if (!TextUtils.isEmpty(teamNotificationText)) {
                notify = TeamNotificationUtil.getTeamNotificationText(data)
            }
        }

        itemView.mTvNotify.text = notify
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}