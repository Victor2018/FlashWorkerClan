package com.flash.worker.lib.common.push

import android.content.Context
import cn.jpush.android.service.PluginVivoMessageReceiver
import com.netease.nimlib.sdk.mixpush.VivoPushMessageReceiver


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: VivoPushReceiver
 * Author: Victor
 * Date: 2021/9/22 10:56
 * Description: 
 * -----------------------------------------------------------------
 */
class VivoPushReceiver: VivoPushMessageReceiver() {
    val receiver = PluginVivoMessageReceiver()

    override fun onReceiveRegId(context: Context?, s: String?) {
        receiver.onReceiveRegId(context,s)
    }
}