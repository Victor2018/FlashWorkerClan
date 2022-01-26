package com.flash.worker.lib.im

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser
import com.flash.worker.lib.im.data.CustomAttachmentType
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomAttachParser
 * Author: Victor
 * Date: 2020/12/29 16:01
 * Description: 
 * -----------------------------------------------------------------
 */
class CustomAttachParser: MsgAttachmentParser {
    companion object {
        const val KEY_TYPE = "type"
        const val KEY_DATA = "data"

        fun packData(type: Int, data: JSONObject?): String? {
            val obj = JSONObject()
            obj.put(KEY_TYPE,type)
            if (data != null) {
                obj.put(KEY_DATA,data)
            }
            return obj.toString()
        }
    }

    override fun parse(attach: String?): MsgAttachment? {
        var attachment: AbsAttachment? = null
        try {
            val obj = JSONObject(attach)
            val type = obj.getInt(KEY_TYPE)
            val data = obj.getJSONObject(KEY_DATA)
            attachment = when (type) {
                CustomAttachmentType.RESUME -> {
                    ResumeAttachment()
                }
                CustomAttachmentType.JOB -> {
                    JobAttachment()
                }
                CustomAttachmentType.TALENT_RELEASE -> {
                    TalentReleaseAttachment()
                }
                CustomAttachmentType.TASK -> {
                    TaskAttachment()
                }
                else -> DefaultCustomAttachment()
            }
            attachment?.fromJson(data)
        } catch (e: Exception) {
        }

        return attachment
    }


}