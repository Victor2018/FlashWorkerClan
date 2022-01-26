package com.flash.worker.lib.im

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsAttachment
 * Author: Victor
 * Date: 2020/12/29 16:02
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsAttachment(var type: Int): MsgAttachment {

    var subMsgType: Int = 0//子消息类型
    var title: String? = null// 消息标题

    override fun toJson(send: Boolean): String? {
        return CustomAttachParser.packData(type, packData())
    }

    fun fromJson(data: JSONObject?) {
        data?.let { parseData(it) }
    }

    protected abstract fun parseData(data: JSONObject?)

    protected abstract fun packData(): JSONObject?
}