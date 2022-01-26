package com.flash.worker.lib.common.push

import android.content.Context
import android.content.Intent
import cn.jpush.android.service.PluginMeizuPlatformsReceiver
import com.meizu.cloud.pushsdk.MzPushMessageReceiver
import com.meizu.cloud.pushsdk.handler.MzPushMessage
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder
import com.meizu.cloud.pushsdk.platform.message.*
import com.netease.nimlib.sdk.mixpush.MeiZuPushReceiver


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MZPushReceiver
 * Author: Victor
 * Date: 2021/9/18 11:45
 * Description: 
 * -----------------------------------------------------------------
 */
class MZPushReceiver: MeiZuPushReceiver() {
    val TAG = "MZPushReceiver"
    val receiver = PluginMeizuPlatformsReceiver()

    override fun onNotificationArrived(context: Context?, mzPushMessage: MzPushMessage?) {
        super.onNotificationArrived(context, mzPushMessage)
        receiver.onNotificationClicked(context,mzPushMessage)
    }

    override fun onNotificationClicked(context: Context?, mzPushMessage: MzPushMessage?) {
        receiver.onNotificationClicked(context,mzPushMessage)
    }

    override fun onNotificationDeleted(context: Context?, mzPushMessage: MzPushMessage?) {
        receiver.onNotificationClicked(context,mzPushMessage)
    }

    override fun onRegister(context: Context?, pushId: String?) {
        receiver.onRegister(context,pushId)
    }

    override fun onUnRegister(context: Context?, success: Boolean) {
        receiver.onUnRegister(context, success)
    }

    override fun onPushStatus(context: Context?, pushSwitchStatus: PushSwitchStatus?) {
        receiver.onPushStatus(context, pushSwitchStatus)
    }

    override fun onRegisterStatus(context: Context?, registerStatus: RegisterStatus?) {
        receiver.onRegisterStatus(context, registerStatus)
    }

    override fun onUnRegisterStatus(
        context: Context?,
        unRegisterStatus: UnRegisterStatus?
    ) {
        receiver.onUnRegisterStatus(context, unRegisterStatus)
    }

    override fun onSubTagsStatus(context: Context?, subTagsStatus: SubTagsStatus?) {
        receiver.onSubTagsStatus(context, subTagsStatus)
    }

    override fun onSubAliasStatus(context: Context?, subAliasStatus: SubAliasStatus?) {
        receiver.onSubAliasStatus(context, subAliasStatus)
    }

    override fun onUpdateNotificationBuilder(pushNotificationBuilder: PushNotificationBuilder?) {
        receiver.onUpdateNotificationBuilder(pushNotificationBuilder)
    }

    override fun onMessage(context: Context?, s: String?) {
        receiver.onMessage(context, s)
    }

}