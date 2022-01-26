package com.flash.worker.lib.im

import com.flash.worker.lib.im.data.CustomAttachmentType
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskAttachment
 * Author: Victor
 * Date: 2021/5/11 11:48
 * Description: 任务消息
 * -----------------------------------------------------------------
 */
class TaskAttachment: AbsAttachment(CustomAttachmentType.TASK) {

    val KEY_SUB_MSG = "subMsgKey"
    val KEY_TITLE = "titleKey"
    val KEY_TASK_DATA = "taskDataKey"

    var taskData: String? = null//任务详情json数据

    override fun parseData(data: JSONObject?) {
        subMsgType = data?.getInt(KEY_SUB_MSG) ?: 0
        title = data?.getString(KEY_TITLE) ?: ""
        taskData = data?.getString(KEY_TASK_DATA)
    }

    override fun packData(): JSONObject? {
        var data = JSONObject()
        try {
            data.put(KEY_SUB_MSG,subMsgType)
            data.put(KEY_TITLE,title)
            data.put(KEY_TASK_DATA,taskData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}