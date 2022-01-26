package com.flash.worker.module.message.view.holder

import android.text.style.ImageSpan
import android.view.View
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.im.*
import com.flash.worker.module.message.R
import com.flash.worker.module.message.util.MoonUtil
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.RecentContact
import kotlinx.android.synthetic.main.rv_recent_contact_cell.view.*
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecentContactContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class RecentContactContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: RecentContact?,count: Int) {
        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.contactId, itemView.mTvNickName, itemView.mCivAvatar,R.mipmap.ic_avatar)
        itemView.mTvContent.text = data?.content

        TimeUtils.bindShowTime(itemView.mTvDate, data?.time)

        var unreadCount = data?.unreadCount ?: 0
        var unreadCountTxt = data?.unreadCount.toString()
        if (unreadCount > 99) {
            unreadCountTxt = "99+"
        }
        itemView.mTvUnReadCount.visibility = if (unreadCount > 0) View.VISIBLE else View.GONE
        itemView.mTvUnReadCount.text = unreadCountTxt

        if (adapterPosition == count - 1) {
            itemView.line_bottom.visibility = View.INVISIBLE
        } else {
            itemView.line_bottom.visibility = View.VISIBLE
        }

        if (data?.msgType == MsgTypeEnum.custom) {
            var msgAttachment = data.attachment
            if (msgAttachment is AbsAttachment) {
                itemView.mTvContent.text = msgAttachment.title
            }
        } else if (data?.msgType == MsgTypeEnum.tip) {
            itemView.mTvContent.text = getDigestOfTipMsg(data)
        } else {
            MoonUtil.identifyFaceExpression(
                App.get(),
                itemView.mTvContent,
                data?.content,
                ImageSpan.ALIGN_BOTTOM
            )
            //导致点击事件冲突
//            itemView.mTvContent.movementMethod = LinkMovementMethod.getInstance()
        }

        val isStickTopSession = NimMessageManager.instance.isStickTopSession(data)

        if (isStickTopSession) {
            itemView.setBackgroundColor(ResUtils.getColorRes(R.color.color_F2F2F2))
        } else {
            itemView.setBackgroundColor(ResUtils.getColorRes(R.color.white))

        }

        setRecentContactStatus(data)

    }

    fun getDigestOfTipMsg(recent: RecentContact): String? {
        val msgId = recent.recentMessageId
        val uuids: MutableList<String> = ArrayList(1)
        uuids.add(msgId)
        val msgs = NIMClient.getService(MsgService::class.java)
                .queryMessageListByUuidBlock(uuids)
        if (msgs != null && !msgs.isEmpty()) {
            val msg = msgs[0]
            return msg.content
        }
        return null
    }

    private fun setRecentContactStatus (recent: RecentContact?) {
        when (recent?.msgStatus) {
            MsgStatusEnum.fail -> {
                itemView.mIvMsgSendFailed.visibility = View.VISIBLE
                itemView.mIvMsgSendFailed.setImageResource(R.mipmap.nim_ic_failed)
            }
            MsgStatusEnum.sending -> {
                itemView.mIvMsgSendFailed.visibility = View.VISIBLE
                itemView.mIvMsgSendFailed.setImageResource(R.mipmap.nim_recent_contact_ic_sending)
            }
            else -> {
                itemView.mIvMsgSendFailed.visibility = View.GONE
            }
        }
    }
}