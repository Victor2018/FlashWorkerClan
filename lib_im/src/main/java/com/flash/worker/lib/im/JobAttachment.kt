package com.flash.worker.lib.im

import com.flash.worker.lib.im.data.CustomAttachmentType
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobAttachment
 * Author: Victor
 * Date: 2021/5/11 11:48
 * Description: 岗位消息
 * -----------------------------------------------------------------
 */
class JobAttachment: AbsAttachment(CustomAttachmentType.JOB) {

    val KEY_SUB_MSG = "subMsgKey"
    val KEY_TITLE = "titleKey"
    val KEY_JOB_DATA = "jobDataKey"

    var jobData: String? = null//岗位详情json数据

    override fun parseData(data: JSONObject?) {
        subMsgType = data?.getInt(KEY_SUB_MSG) ?: 0
        title = data?.getString(KEY_TITLE) ?: ""
        jobData = data?.getString(KEY_JOB_DATA)
    }

    override fun packData(): JSONObject? {
        var data = JSONObject()
        try {
            data.put(KEY_SUB_MSG,subMsgType)
            data.put(KEY_TITLE,title)
            data.put(KEY_JOB_DATA,jobData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}