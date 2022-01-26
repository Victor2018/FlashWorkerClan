package com.flash.worker.lib.common.push

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.NimSDKOptionConfig
import com.flash.worker.lib.common.util.ToastUtils
import com.netease.nimlib.sdk.NimIntent
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import com.netease.nimlib.sdk.mixpush.MixPushMessageHandler
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimMixPushMessageHandler
 * Author: Victor
 * Date: 2021/3/12 11:43
 * Description: 
 * -----------------------------------------------------------------
 */
class NimMixPushMessageHandler: MixPushMessageHandler {
    val TAG = "NimMixPushMessageHandler"
    val PAYLOAD_SESSION_ID = "sessionID"
    val PAYLOAD_SESSION_TYPE = "sessionType"

    // 对于华为推送，这个方法并不能保证一定会回调
    override fun onNotificationClicked(
        context: Context,
        payload: Map<String, String>
    ): Boolean {
        Loger.e(TAG, "onNotificationClicked-rev pushMessage payload $payload")
        val sessionId = payload[PAYLOAD_SESSION_ID]
        val type = payload[PAYLOAD_SESSION_TYPE]
        Loger.e(TAG, "onNotificationClicked-sessionId = $sessionId")
        Loger.e(TAG, "onNotificationClicked-type = $type")
        return if (sessionId != null && type != null) {
            val typeValue = Integer.valueOf(type)
            val imMessages = ArrayList<IMMessage>()
            val imMessage = MessageBuilder.createEmptyMessage(
                sessionId,
                SessionTypeEnum.typeOfValue(typeValue),
                0
            )
            imMessages.add(imMessage)
            val notifyIntent = Intent()
            notifyIntent.component = initLaunchComponent(context)
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            notifyIntent.action = Intent.ACTION_VIEW
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 必须
            notifyIntent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessages)
            context.startActivity(notifyIntent)
            true
        } else {
            val imMessages = ArrayList<IMMessage>()
            val imMessage = MessageBuilder.createEmptyMessage(
                sessionId,
                SessionTypeEnum.None,
                0
            )
            imMessages.add(imMessage)
            val notifyIntent = Intent()
            notifyIntent.component = initLaunchComponent(context)
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            notifyIntent.action = Intent.ACTION_VIEW
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 必须
            notifyIntent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessages)
            context.startActivity(notifyIntent)
            false
        }
    }

    private fun initLaunchComponent(context: Context): ComponentName? {
        val launchComponent: ComponentName?
        val config: StatusBarNotificationConfig = NimSDKOptionConfig.getStatusBarNotificationConfig()
        val entrance = config.notificationEntrance
        launchComponent = entrance?.let { ComponentName(context, it) }
            ?: context.packageManager.getLaunchIntentForPackage(context.packageName)?.getComponent()
        return launchComponent
    }

    // 将音视频通知 Notification 缓存，清除所有通知后再次弹出 Notification，避免清除之后找不到打开正在进行音视频通话界面的入口
    override fun cleanMixPushNotifications(pushType: Int): Boolean {
        val context: Context = App.get()
        val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager?.cancelAll()
        /*val nos: SparseArray<Notification> = AVChatKit.getNotifications()
        if (nos != null) {
            var key = 0
            for (i in 0 until nos.size()) {
                key = nos.keyAt(i)
                manager.notify(key, nos[key])
            }
        }*/
        return true
    }
}