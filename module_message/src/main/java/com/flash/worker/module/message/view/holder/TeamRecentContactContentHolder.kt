package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.text.style.ImageSpan
import android.view.View
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.util.Loger
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
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import kotlinx.android.synthetic.main.rv_recent_contact_cell.view.*
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TeamRecentContactContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class TeamRecentContactContentHolder(itemView: View) : ContentViewHolder(itemView) {
    val TAG = "TeamRecentContactContentHolder"

    fun bindData (data: RecentContact?,count: Int) {
        val team = TeamNotificationUtil.getTeam(data?.contactId)
        if (team?.type == TeamTypeEnum.Advanced) {
            ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,R.mipmap.ic_team_avatar)
            itemView.mTvNickName.text = team?.name
        }

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

        var content: String? = null

        if (data?.msgType== MsgTypeEnum.custom) {
            var msgAttachment = data?.attachment
            if (msgAttachment is AbsAttachment) {
                content = msgAttachment.title
            }
        } else if (data?.msgType == MsgTypeEnum.tip) {
            content = getDigestOfTipMsg(data,false)
        } else if (data?.msgType == MsgTypeEnum.notification) {
            content = getDigestOfTipMsg(data,true)
        } else {
            content = getDigestOfTipMsg(data,false)
        }

        itemView.mTvContent.text = content

        MoonUtil.identifyFaceExpression(
            App.get(),
            itemView.mTvContent,
            content,
            ImageSpan.ALIGN_BOTTOM
        )

        val isStickTopSession = NimMessageManager.instance.isStickTopSession(data)

        if (isStickTopSession) {
            itemView.setBackgroundColor(ResUtils.getColorRes(R.color.color_F2F2F2))
        } else {
            itemView.setBackgroundColor(ResUtils.getColorRes(R.color.white))
        }

        setRecentContactStatus(data)

    }

    fun getDigestOfTipMsg(recent: RecentContact?,isNotify: Boolean): String? {
        Loger.e(TAG,"bindData-isNotify = ${isNotify}")
        val msgId = recent?.recentMessageId ?: ""
        val uuids: MutableList<String> = ArrayList(1)
        uuids.add(msgId)
        val msgs = NIMClient.getService(MsgService::class.java)
            .queryMessageListByUuidBlock(uuids)

        if (msgs != null && !msgs.isEmpty()) {
            val msg = msgs[0]

            if (isNotify) {
                return TeamNotificationUtil.getTeamNotificationText(msg)
            } else {
                var mNickName = App.get().getUserInfo()?.username
                var nickname = msg.fromNick ?: ""
//                var content = msg.content ?: ""
                var content = recent?.content ?: ""

                if (TextUtils.equals(mNickName,nickname)) {
                    return "我:$content"
                }
                return "$nickname:$content"
            }

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