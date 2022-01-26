package com.flash.worker.lib.coremodel.util

import android.net.Uri

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WebConfig
 * Author: Victor
 * Date: 2020/12/9 11:10
 * Description: 
 * -----------------------------------------------------------------
 */
object WebConfig {
    const val DEV_BASE_URL = "https://appapi-dev.shangongzu.com/ms-app/"
//    const val DEV_BASE_URL = "http://192.168.2.43:9025/ms-app/"
    const val UAT_BASE_URL = "https://appapi-fat.shangongzu.com/ms-app/"
    const val ONLINE_BASE_URL = "https://appapi.shangongzu.com/ms-app/"

    const val PAGE_SIZE = 20

    fun getBaseUrl(): String? {
        if (AppConfig.MODEL_UAT) {
            return UAT_BASE_URL
        }

        if (AppConfig.MODEL_ONLINE) {
            return ONLINE_BASE_URL
        }
        return DEV_BASE_URL
    }

    fun getServerIp(): String? {
        val uri: Uri = Uri.parse(getBaseUrl())
        return uri.host
    }

    fun getRequestUrl(api: String): String? {
        return getBaseUrl() + api
    }
}