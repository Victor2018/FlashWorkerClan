package com.flash.worker.lib.im

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.model.MessageReceipt


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimMessageReceiptObserver
 * Author: Victor
 * Date: 2020/12/29 15:59
 * Description: 
 * -----------------------------------------------------------------
 */
class NimMessageReceiptObserver: Observer<MutableList<MessageReceipt>> {
    val TAG = "NimMessageReceiptObserver"

    @SuppressLint("LongLogTag")
    override fun onEvent(messageReceipts: MutableList<MessageReceipt>?) {
        Log.e(TAG, "onEvent()......")
        if (messageReceipts != null && messageReceipts.size > 0) {
            for (messageReceipt in messageReceipts) {
                val receipt: String = Gson().toJson(messageReceipt)
                Log.e(TAG, "onEvent()......receipt = $receipt")
            }
        }
    }
}