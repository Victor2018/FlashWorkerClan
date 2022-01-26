package com.flash.worker.lib.im

import android.util.Log
import com.flash.worker.lib.im.NimMessageManager
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.StatusCode


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimOnlineObserver
 * Author: Victor
 * Date: 2020/12/29 15:58
 * Description: 
 * -----------------------------------------------------------------
 */
class NimOnlineObserver: Observer<StatusCode> {
    val TAG = "NimOnlineObserver"

    override fun onEvent(t: StatusCode?) {
        val code = NIMClient.getStatus()
        Log.i(TAG, "Nim Online Status: " + code.name)
        if (code.wontAutoLogin()) {
            //被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
            //需要重新登陆
            Log.i(TAG, "Nim Logout...")
        } else if (code == StatusCode.UNLOGIN) {
            //重新登录
//            RxBus.get().post(BusActions.NIM_STATE_UNLOGIN, 0)
            NimMessageManager.instance.loginRetryDelay()
        }
    }
}