package com.flash.worker.lib.im

import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultCustomAttachment
 * Author: Victor
 * Date: 2020/12/29 16:07
 * Description: 
 * -----------------------------------------------------------------
 */
class DefaultCustomAttachment: AbsAttachment(0) {
    private var content: String? = null
    override fun parseData(data: JSONObject?) {
        content = data?.toString()
    }

    override fun packData(): JSONObject? {
        var data: JSONObject? = null
        try {
            data = JSONObject(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}