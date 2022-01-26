package com.flash.worker.lib.im.interfaces

import com.netease.nimlib.sdk.msg.model.IMMessage


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomPushContentProvider
 * Author: Victor
 * Date: 2021/9/23 15:37
 * Description: 用户自定义推送 content 以及 payload 的接口
 * -----------------------------------------------------------------
 */
interface CustomPushContentProvider {
    /**
     * 在消息发出去之前，回调此方法，用户需实现自定义的推送文案
     *
     * @param message
     */
    fun getPushContent(message: IMMessage?): String?

    /**
     * 在消息发出去之前，回调此方法，用户需实现自定义的推送payload，它可以被消息接受者在通知栏点击之后得到
     *
     * @param message
     */
    fun getPushPayload(message: IMMessage?): Map<String?, Any?>?
}