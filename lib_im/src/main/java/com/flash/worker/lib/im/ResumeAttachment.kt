package com.flash.worker.lib.im

import com.flash.worker.lib.im.data.CustomAttachmentType
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeAttachment
 * Author: Victor
 * Date: 2021/5/11 11:48
 * Description: 简历消息
 * -----------------------------------------------------------------
 */
class ResumeAttachment: AbsAttachment(CustomAttachmentType.RESUME) {

    val KEY_SUB_MSG = "subMsgKey"
    val KEY_TITLE = "titleKey"
    val KEY_RESUME_DATA = "resumeDataKey"

    var resumeData: String? = null//消息文本内容

    override fun parseData(data: JSONObject?) {
        subMsgType = data?.getInt(KEY_SUB_MSG) ?: 0
        title = data?.getString(KEY_TITLE) ?: ""
        resumeData = data?.getString(KEY_RESUME_DATA)
    }

    override fun packData(): JSONObject? {
        var data = JSONObject()
        try {
            data.put(KEY_SUB_MSG,subMsgType)
            data.put(KEY_TITLE,title)
            data.put(KEY_RESUME_DATA,resumeData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}