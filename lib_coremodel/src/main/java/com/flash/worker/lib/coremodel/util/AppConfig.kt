package com.flash.worker.lib.coremodel.util

import android.app.Notification
import android.content.Context
import android.util.Log
import cn.jpush.android.api.BasicPushNotificationBuilder
import cn.jpush.android.api.JPushInterface
import com.flash.worker.lib.coremodel.BuildConfig
import com.flash.worker.lib.coremodel.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AppConfig
 * Author: Victor
 * Date: 2020/7/4 下午 02:36
 * Description: 
 * -----------------------------------------------------------------
 */
object AppConfig {
    /**
     * DEBUG 调试模式
     */
    const val MODEL_DEBUG = BuildConfig.MODEL_DEBUG

    /**
     * 开发模式
     */
    const val MODEL_DEV = BuildConfig.MODEL_DEV

    /**
     * UAT测试模式
     */
    const val MODEL_UAT = BuildConfig.MODEL_UAT
    /**
     * 线上模式
     */
    const val MODEL_ONLINE = BuildConfig.MODEL_ONLINE

    /**
     * 编译版本
     */
    const val BUILD_NUMBER = BuildConfig.BUILD_NUMBER

    /**
     * 微信AppID
     */
    const val WECHAT_APP_ID = "wx92207d67d5931463"

    /**
     * qq AppID
     */
    const val QQ_APP_ID = "1111965449"

    const val UMENG_APP_KEY = "60e54a4e2a1a2a58e7caa159"
    const val UMENG_CHANNEL = BuildConfig.UMENG_CHANNEL

    const val SCHEMA = "shangongzu://"

    const val PACKAGE_QQ = "com.tencent.mobileqq"


}