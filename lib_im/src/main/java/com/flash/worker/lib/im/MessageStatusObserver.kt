package com.flash.worker.lib.im

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.google.gson.Gson


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageStatusObserver
 * Author: Victor
 * Date: 2020/12/30 11:01
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageStatusObserver: Observer<IMMessage> {
    val TAG = "MessageStatusObserver"

    private var mNimHandler: Handler? = null
    private var mNimHandlerThread: HandlerThread? = null

    init {
        startMessageStatusTask()
    }

    private fun startMessageStatusTask() {
        mNimHandlerThread = HandlerThread("messageStatusTask")
        mNimHandlerThread?.start()
        mNimHandler = object : Handler(mNimHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {
                dispatchMessage(msg.obj as IMMessage)
            }
        }
    }

    protected fun dispatchMessage(message: IMMessage?) {
        try {
            if (message == null) return
            LiveDataBus.send(IMActions.MESSAGE_STATUS,message)
            LiveDataBus.send(IMActions.REFRESH_MESSAGE_STATUS,message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    protected fun postMessage(recentContact: IMMessage?) {
        if (mNimHandler != null) {
            val msg: Message = mNimHandler?.obtainMessage()!!
            msg.obj = recentContact
            mNimHandler?.sendMessage(msg)
        }
    }

    @SuppressLint("LongLogTag")
    override fun onEvent(message: IMMessage?) {
        if (message == null) {
            return
        }
        val textStr: String = Gson().toJson(message)
        Log.e(TAG, "onEvent-textStr = $textStr")
        postMessage(message)
    }

    fun onDestroy() {
        mNimHandlerThread?.quit()
        mNimHandlerThread = null
    }

}