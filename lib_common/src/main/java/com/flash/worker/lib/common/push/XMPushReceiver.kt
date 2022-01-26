package com.flash.worker.lib.common.push

import android.content.Context
import cn.jpush.android.service.PluginXiaomiPlatformsReceiver
import com.flash.worker.lib.common.util.Loger
import com.netease.nimlib.sdk.mixpush.MiPushMessageReceiver
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: XMPushReceiver
 * Author: Victor
 * Date: 2021/9/16 9:56
 * Description: 
 * -----------------------------------------------------------------
 */
class XMPushReceiver: MiPushMessageReceiver() {
    val TAG = "XMPushReceiver"
    val receiver = PluginXiaomiPlatformsReceiver()

    override fun onReceivePassThroughMessage(context: Context?, message: MiPushMessage?) {
        Loger.e(TAG,"onReceivePassThroughMessage()......")
        receiver.onReceivePassThroughMessage(context, message)
    }

    override fun onNotificationMessageClicked(context: Context?, message: MiPushMessage?) {
        Loger.e(TAG,"onNotificationMessageClicked()......")
        receiver.onNotificationMessageClicked(context, message)
    }

    override fun onNotificationMessageArrived(context: Context?, message: MiPushMessage?) {
        Loger.e(TAG,"onNotificationMessageArrived()......")
        receiver.onNotificationMessageArrived(context, message)
    }

    override fun onCommandResult(context: Context?, message: MiPushCommandMessage?) {
        Loger.e(TAG,"onCommandResult()......")
        receiver.onCommandResult(context, message)
    }

    override fun onReceiveRegisterResult(
        context: Context?,
        message: MiPushCommandMessage?
    ) {
        Loger.e(TAG,"onReceiveRegisterResult()......")
        receiver.onReceiveRegisterResult(context, message)
    }

}