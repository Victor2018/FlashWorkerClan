package com.flash.worker.lib.common.push

import cn.jpush.android.service.PluginHuaweiPlatformsService
import com.huawei.hms.push.RemoteMessage
import com.netease.nimlib.sdk.mixpush.HWPushMessageService


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HMSPushService
 * Author: Victor
 * Date: 2021/9/22 9:46
 * Description: 
 * -----------------------------------------------------------------
 */
class HMSPushService: HWPushMessageService() {
    val service = PluginHuaweiPlatformsService()

    override fun onNewToken(s: String?) {
        service.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        service.onMessageReceived(remoteMessage)
    }

    override fun onMessageSent(s: String?) {
        service.onMessageSent(s)
    }

    override fun onSendError(s: String?, e: Exception?) {
        service.onSendError(s, e)
    }

    override fun onDeletedMessages() {
        service.onDeletedMessages()
    }
}