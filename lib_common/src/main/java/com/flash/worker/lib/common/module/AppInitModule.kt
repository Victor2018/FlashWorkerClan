package com.flash.worker.lib.common.module

import android.content.Context
import com.flash.worker.lib.common.push.JPushOpenHelper
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.util.HttpUtil


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AppInitModule
 * Author: Victor
 * Date: 2021/12/15 15:45
 * Description: 第三方sdk初始化涉及到设备的信息采集必须在同意隐私政策之后调用
 * -----------------------------------------------------------------
 */
object AppInitModule {

    /**
     * 初始化第三方sdk
     */
    fun initThirdSdk (context: Context) {
        //极光推送
        JPushOpenHelper.initJPush(context)
        //友盟推送
        UMengEventModule.initSdk(context)
        //初始化http请求头数据
        ApiClient.addHeader(JsonUtils.toJSONString(HttpUtil.getHttpHeaderParm(context)))
    }
}