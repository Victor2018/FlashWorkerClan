package com.flash.worker.lib.im

import android.util.Log
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.netease.nimlib.sdk.RequestCallback


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimLoginCallBack
 * Author: Victor
 * Date: 2020/12/29 14:48
 * Description: 
 * -----------------------------------------------------------------
 */
class NimLoginCallBack: RequestCallback<Any> {
    val TAG = "NimLoginCallBack"

    override fun onSuccess(param: Any?) {
        Log.e(TAG, "Nim Login Success...")
        LiveDataBus.send(IMActions.NIM_LOGIN_SUCCESS)
        LiveDataBus.send(IMActions.REFRESH_UNREAD_COUNT)
    }

    override fun onFailed(code: Int) {
        Log.e(TAG, "Nim Login Failed ...$code")
        if (code == 408) {
            NimMessageManager.instance.loginRetryDelay()
        }
    }

    override fun onException(exception: Throwable?) {
        Log.e(TAG, "Nim Login Exception...$exception")
    }
}