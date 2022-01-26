package com.flash.worker.lib.im

import android.text.TextUtils
import android.util.Log
import com.amap.api.services.core.PoiItem
import com.flash.worker.lib.im.data.CustomAttachmentType
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamBeInviteModeEnum
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import com.netease.nimlib.sdk.team.model.CreateTeamResult
import java.io.File
import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimMessageUtil
 * Author: Victor
 * Date: 2020/12/29 16:29
 * Description: 
 * -----------------------------------------------------------------
 */
object NimMessageUtil {
    private const val TAG = "NimMessageUtil"

    /**
     * 重发消息
     */
    fun reSendMessage(message: IMMessage?): IMMessage? {
        NimMessageManager.instance.sendMessage(message,true)
        return message
    }

    /**
     * 发送文本消息
     * @param netid
     * @param msg
     * @return
     */
    fun sendTxtMessage(netid: String?, msg: String?,reSend:Boolean = false): IMMessage? {
        val message = obtainTxtChat(netid, msg)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message,reSend)
        return message
    }

    /**
     * 发送图片消息
     * @param account
     * @param imgPath
     * @return
     */
    fun sendImageMessage(account: String?, imgPath: String?): IMMessage? {
        val message = obtainImageChat(account, imgPath)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    /**
     * 发送简历消息
     * @return
     */
    fun sendResumeMessage(account: String?,data: String?): IMMessage? {
        val message = obtainResumeChat(account,data)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    /**
     * 发送岗位消息
     * @return
     */
    fun sendJobMessage(account: String?,data: String?,title: String?): IMMessage? {
        val message = obtainJobChat(account,data,title)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    /**
     * 发送人才接活消息
     * @return
     */
    fun sendTalentReleaseMessage(account: String?,data: String?): IMMessage? {
        val message = obtainTalentReleaseChat(account,data)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    /**
     * 发送任务消息
     * @return
     */
    fun sendTaskMessage(account: String?,data: String?,title: String?): IMMessage? {
        val message = obtainTaskChat(account,data,title)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    /**
     * 发送位置消息
     * @param account
     * @param poiItem
     * @return
     */
    fun sendLocationMessage(account: String?, poiItem: PoiItem?): IMMessage? {
        val message = obtainLocationChat(account, poiItem)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    fun sendLocationMessage(account: String?,
                            latitude: Double?,
                            longitude: Double?,
                            address: String?): IMMessage? {
        val message = obtainLocationChat(account, latitude,longitude,address)
        appendPushConfig(message)
        NimMessageManager.instance.sendMessage(message)
        return message
    }

    fun appendPushConfig(message: IMMessage?) {
        var pushPayload= message?.pushPayload?: HashMap()

        val vivoField: MutableMap<String, Any> = HashMap()
        vivoField["classification"] = 1//消息类型 0：运营类消息，1：系统类消息

        pushPayload["vivoField"] = vivoField

        val config = getMessageConfig()
        message?.config = config
    }

    /**
     * 包装私聊消息
     */
    fun obtainTxtChat(account: String?, msg: String?): IMMessage {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        // 创建一个文本消息
        return MessageBuilder.createTextMessage(account, sessionType, msg)
    }

    fun obtainImageChat(account: String?, imgPath: String?): IMMessage? {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        // 示例图片，需要开发者在相应目录下有图片
        val file = File(imgPath)
        // 创建一个图片消息
        return MessageBuilder.createImageMessage(account, sessionType, file, file.name)
    }

    fun obtainResumeChat(account: String?,data: String?): IMMessage? {
        var content = "给你发送了一份简历"
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        val attachment = ResumeAttachment()
        attachment.title = "[简历]"
        attachment.subMsgType = CustomAttachmentType.RESUME
        attachment.resumeData = data

        val config = getMessageConfig()

        var message = MessageBuilder.createCustomMessage(
            account,
            sessionType,
            content,
            attachment,
            config
        )

        return message
    }

    fun obtainJobChat(account: String?,data: String?,title: String?): IMMessage? {
        var contentTitle = title?.replace("[","")?.replace("]","")
        var content = "给你发送了一个${contentTitle}"
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        val attachment = JobAttachment()
        attachment.title = title
        attachment.subMsgType = CustomAttachmentType.JOB
        attachment.jobData = data

        val config = getMessageConfig()

        var message = MessageBuilder.createCustomMessage(
            account,
            sessionType,
            content,
            attachment,
            config
        )

        return message
    }

    fun obtainTalentReleaseChat(account: String?,data: String?): IMMessage? {
        var content = "想和你沟通这个接活"
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        val attachment = TalentReleaseAttachment()
        attachment.title = "[接活信息]"
        attachment.subMsgType = CustomAttachmentType.TALENT_RELEASE
        attachment.talentReleaseData = data

        val config = getMessageConfig()

        var message = MessageBuilder.createCustomMessage(
            account,
            sessionType,
            content,
            attachment,
            config
        )

        return message
    }

    fun obtainTaskChat(account: String?,data: String?,title: String?): IMMessage? {
        var contentTitle = title?.replace("[","")?.replace("]","")
        var content = "给你发送了一个${contentTitle}"
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }
        val attachment = TaskAttachment()
        attachment.title = title
        attachment.subMsgType = CustomAttachmentType.TASK
        attachment.taskData = data

        val config = getMessageConfig()

        var message = MessageBuilder.createCustomMessage(
            account,
            sessionType,
            content,
            attachment,
            config
        )

        return message
    }

    fun obtainLocationChat(account: String?, poiItem: PoiItem?): IMMessage? {
        val latitude = poiItem?.latLonPoint?.latitude ?: 0.0
        val longitude = poiItem?.latLonPoint?.longitude ?: 0.0
        val address = "${poiItem?.provinceName}${poiItem?.cityName}${poiItem?.adName}${poiItem?.snippet}"

        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }

        // 创建地理位置信息
        var message = MessageBuilder.createLocationMessage(
            account, sessionType,
            latitude, longitude, address
        )

        return message
    }

    fun obtainLocationChat(
        account: String?,
        latitude: Double?,
        longitude: Double?,
        address: String?): IMMessage? {

        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        if (isTeamChat(account)) {
            sessionType = SessionTypeEnum.Team
        }

        // 创建地理位置信息
        var message = MessageBuilder.createLocationMessage(
            account, sessionType,
            latitude ?: 0.0, longitude ?: 0.0, address
        )

        return message
    }

    fun getMessageConfig(): CustomMessageConfig {
        val config = CustomMessageConfig()
        config.enablePush = NimMessageManager.instance.isEnablePush()
        return config
    }

    /**
     * 是否是我发的消息
     * @param fromAccount
     * @return
     */
    fun isMessageSend(message: IMMessage?): Boolean {
        return message?.direct == MsgDirectionEnum.Out
    }

    // 判断消息方向，是否是接收到的消息
    fun isReceivedMessage(message: IMMessage?): Boolean {
        return message?.direct == MsgDirectionEnum.In
    }

    /**
     * 是否是我的消息
     */
    fun isMyMessage(message: IMMessage, userAccount: String?): Boolean {
        return TextUtils.equals(message.sessionId,userAccount)
    }

    /**
     * 创建高级群
     */
    fun createAdvancedTeam(memberAccounts: List<String?>?) {
        val teamName = "快递交流群"
        // 创建群
        val type = TeamTypeEnum.Advanced
        val fields = HashMap<TeamFieldEnum, Serializable>()
        fields[TeamFieldEnum.Name] = teamName
        fields[TeamFieldEnum.BeInviteMode] = TeamBeInviteModeEnum.NoAuth
        NIMClient.getService(TeamService::class.java).createTeam(
            fields, type, "",
            memberAccounts
        ).setCallback(
            object : RequestCallback<CreateTeamResult> {
                override fun onSuccess(result: CreateTeamResult) {
                    Log.e(TAG,"create team success, team id =" + result.team.id + ", now begin to update property...")
//                    sendCreateTeamSuccessTip(result.team.id)
                }

                override fun onFailed(code: Int) {
                    val tip: String
                    tip = if (code == ResponseCode.RES_TEAM_ECOUNT_LIMIT.toInt()) {
                        "邀请失败，成员人数上限为200人"
                    } else if (code == ResponseCode.RES_TEAM_LIMIT.toInt()) {
                        "创建失败，创建群数量达到限制"
                    } else {
                        "创建失败, code = $code"
                    }
                    Log.e(TAG, "create team error-code = $code")
                    Log.e(TAG, "create team error-tip =  $tip")
                }

                override fun onException(exception: Throwable) {
                }
            }
        )
    }

    fun sendCreateTeamSuccessTip(teamId: String?) {

        // 演示：向群里插入一条Tip消息，使得该群能立即出现在最近联系人列表（会话列表）中，满足部分开发者需求
        val content: MutableMap<String, Any> = java.util.HashMap(1)
        var contentStr = "成功创建高级群"
        content["content"] = contentStr
        val msg = MessageBuilder.createTipMessage(teamId, SessionTypeEnum.Team)
        msg.remoteExtension = content
        msg.content = contentStr
        val config = CustomMessageConfig()
        config.enableUnreadCount = false
        msg.config = config
        msg.status = MsgStatusEnum.success
//        NIMClient.getService(MsgService::class.java).saveMessageToLocal(msg, true)
//        NIMClient.getService(MsgService::class.java).sendMessage(msg, true)

        sendTxtMessage(teamId,contentStr)
    }

    /**
     * 是否是群聊
     */
    fun isTeamChat (account: String?): Boolean {
        var teamNick = TeamNotificationUtil.getTeamNickName(account)
        return !TextUtils.isEmpty(teamNick)
    }

}