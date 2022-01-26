package com.flash.worker.lib.im

import com.flash.worker.lib.im.data.CustomAttachmentType
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseAttachment
 * Author: Victor
 * Date: 2021/5/11 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseAttachment: AbsAttachment(CustomAttachmentType.TALENT_RELEASE) {

    val KEY_SUB_MSG = "subMsgKey"
    val KEY_TITLE = "titleKey"
    val KEY_TALENT_RELEASE_DATA = "TalentReleaseDataKey"

    var talentReleaseData: String? = null//岗位详情json数据

    override fun parseData(data: JSONObject?) {
        subMsgType = data?.getInt(KEY_SUB_MSG) ?: 0
        title = data?.getString(KEY_TITLE) ?: ""
        talentReleaseData = data?.getString(KEY_TALENT_RELEASE_DATA)
    }

    override fun packData(): JSONObject? {
        var data = JSONObject()
        try {
            data.put(KEY_SUB_MSG,subMsgType)
            data.put(KEY_TITLE,title)
            data.put(KEY_TALENT_RELEASE_DATA,talentReleaseData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}