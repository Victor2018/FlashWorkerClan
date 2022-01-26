package com.flash.worker.lib.im

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment
import com.netease.nimlib.sdk.msg.constant.NotificationType
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamAllMuteModeEnum
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment
import java.lang.StringBuilder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TeamNotificationHelper
 * Author: Victor
 * Date: 2021/11/16 14:24
 * Description: 
 * -----------------------------------------------------------------
 */
object TeamNotificationUtil {
    const val TAG = "TeamNotificationHelper"

    fun getTeamNickName(contactId: String?): String? {
        if (TextUtils.isEmpty(contactId)) return null
        val team = NIMClient.getService(TeamService::class.java).queryTeamBlock(contactId)
        if (team?.type == TeamTypeEnum.Advanced) {
            return team.name
        }
        return null
    }

    fun getTeam(sessionId: String?): Team? {
        if (TextUtils.isEmpty(sessionId)) return null
        val team = NIMClient.getService(TeamService::class.java).queryTeamBlock(sessionId)
        if (team?.type == TeamTypeEnum.Advanced) {
            return team
        }
        return null
    }
    fun getTeamMemberCount(contactId: String?): Int {
        if (TextUtils.isEmpty(contactId)) return 0
        val team = NIMClient.getService(TeamService::class.java).queryTeamBlock(contactId)
        if (team?.type == TeamTypeEnum.Advanced) {
            return team.memberCount
        }
        return 0
    }

    fun getTeamMemberNick (sessionId: String?,account: String?): String? {
        if (TextUtils.isEmpty(sessionId)) return null
        val team = NIMClient.getService(TeamService::class.java).queryTeamBlock(sessionId)
        if (team?.type == TeamTypeEnum.Advanced) {
            val member = NIMClient.getService(TeamService::class.java).queryTeamMemberBlock(team.id,account)
            if (!TextUtils.isEmpty(member?.teamNick)) {
                return member.teamNick
            }
        }

        return account
    }

    fun getNimUserNick (account: String?): String? {
        if (TextUtils.isEmpty(account)) return null
        var nimUserInfo = NimMessageManager.instance.getNimUserInfo(account)
        var nickName = nimUserInfo?.name
        if (!TextUtils.isEmpty(nickName)) {
            var mNimAccount = NimMessageManager.instance.mNimLoginInfo?.account
            if (TextUtils.equals(mNimAccount,account)) {
                return "你"
            }
            return nickName
        }
        return account
    }

    fun getTeamNotificationText(message: IMMessage?): String? {
        var notificationTxt: String? = ""
        var sessionId = message?.sessionId
        var fromAccount = message?.fromAccount
        var notificationAttachment = message?.attachment as NotificationAttachment

        when (notificationAttachment?.type) {
            NotificationType.InviteMember, NotificationType.SUPER_TEAM_INVITE -> {
                notificationTxt = buildInviteMemberNotification(notificationAttachment as MemberChangeAttachment ,sessionId,fromAccount)
            }
            NotificationType.KickMember,NotificationType.SUPER_TEAM_KICK -> {
                notificationTxt = buildKickMemberNotification(notificationAttachment as MemberChangeAttachment,sessionId)
            }
            NotificationType.LeaveTeam,NotificationType.SUPER_TEAM_LEAVE -> {
                notificationTxt = buildLeaveTeamNotification(sessionId,fromAccount)
            }
            NotificationType.DismissTeam,NotificationType.SUPER_TEAM_DISMISS -> {
                notificationTxt = buildDismissTeamNotification(fromAccount)
            }
        }

        return notificationTxt
    }

    fun buildInviteMemberNotification(
        a: MemberChangeAttachment?,
        sessionId: String?,
        fromAccount: String?
    ): String? {
        if (TextUtils.isEmpty(sessionId)) return null
        val sb = StringBuilder()
        var selfName = getNimUserNick(fromAccount)

        sb.append(selfName)
        sb.append("邀请 ")
        sb.append(buildMemberListString(a?.targets, sessionId,fromAccount))
        val team: Team = NIMClient.getService(TeamService::class.java).queryTeamBlock(sessionId)
        if (team == null || team.type == TeamTypeEnum.Advanced) {
            sb.append(" 加入群")
        } else {
            sb.append(" 加入讨论组")
        }
        return sb.toString()
    }

    private fun buildKickMemberNotification(a: MemberChangeAttachment,sessionId: String?): String? {
        if (TextUtils.isEmpty(sessionId)) return null
        val sb = StringBuilder()
        sb.append(buildMemberListString(a.targets, null,"")
        )
        val team: Team? = NIMClient.getService(TeamService::class.java).queryTeamBlock(sessionId)
        if (team == null || team?.type == TeamTypeEnum.Advanced) {
            sb.append(" 已被移出群")
        } else {
            sb.append(" 已被移出讨论组")
        }
        return sb.toString()
    }

    private fun buildLeaveTeamNotification(sessionId: String?,fromAccount: String?): String? {
        val tip: String
        val team: Team = NIMClient.getService(TeamService::class.java).queryTeamBlock(sessionId)
        tip = if (team == null || team.type == TeamTypeEnum.Advanced) {
            " 离开了群"
        } else {
            " 离开了讨论组"
        }
        return getNimUserNick(fromAccount) + tip
    }

    private fun buildDismissTeamNotification(fromAccount: String?): String? {
        return getNimUserNick(fromAccount) + " 解散了群"
    }

    private fun buildMemberListString(members: List<String>?, sessionId: String?,fromAccount: String?): String? {
        val sb = StringBuilder()
        for (account in members!!) {
            if (!TextUtils.isEmpty(fromAccount) && fromAccount == account) {
                continue
            }
//            sb.append(getTeamMemberNick(sessionId,account))
            var nimUserInfo = NimMessageManager.instance.getNimUserInfo(account)
            if (TextUtils.isEmpty(nimUserInfo?.name)) {
                sb.append(nimUserInfo?.account)
            } else {
                sb.append(getNimUserNick(nimUserInfo?.account))
            }
            sb.append(",")
        }
        sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }

}