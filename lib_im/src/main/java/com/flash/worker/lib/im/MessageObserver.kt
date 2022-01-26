package com.flash.worker.lib.im

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.google.gson.Gson


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimMsgController
 * Author: Victor
 * Date: 2020/12/29 14:52
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageObserver: Observer<MutableList<IMMessage>> {
    val TAG = "NimMsgController"
    private var mNimHandler: Handler? = null
    private var mNimHandlerThread: HandlerThread? = null

    init {
        startPayTask()
    }

    private fun startPayTask() {
        mNimHandlerThread = HandlerThread("NimMsgTask")
        mNimHandlerThread?.start()
        mNimHandler = object : Handler(mNimHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {
                dispatchNimMessage(msg.obj as IMMessage)
            }
        }
    }

    protected fun postMessage(message: IMMessage?) {
        if (mNimHandler != null) {
            val msg: Message = mNimHandler?.obtainMessage()!!
            msg.obj = message
            mNimHandler?.sendMessage(msg)
        }
    }

    protected fun dispatchNimMessage(message: IMMessage?) {
        try {
            if (message == null) return
            dispatchTypeMessage(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dispatchTypeMessage(message: IMMessage) {
        val msgType = message.msgType
        Log.e(TAG, "dispatchTypeMessage-msgType = $msgType")
        when (msgType) {
            MsgTypeEnum.text -> dispatchCommonMessage(message)
            MsgTypeEnum.image -> dispatchCommonMessage(message)
            else -> {
                dispatchCommonMessage(message)
            }
        }
    }

    /**
     * 处理云信常规消息
     */
    protected fun dispatchCommonMessage(message: IMMessage?) {
        LiveDataBus.send(IMActions.NEW_MESSAGE,message)
        LiveDataBus.send(IMActions.REFRESH_UNREAD_COUNT)
        Log.e(TAG, "dispatchCommonMessage-nimMessage = " + Gson().toJson(message))
    }

    /**
     * 处理云信系统消息
     */
    protected fun dispatchSystemMessage(
        message: IMMessage?,
        nimMessage: IMMessage,
        text: String
    ) {
        Log.e(TAG, "dispatchSystemMessage-text = $text")
    }

    override fun onEvent(imMessages: MutableList<IMMessage>?) {
        if (imMessages == null) {
            return
        }
        for (message in imMessages) {
            val textStr: String = Gson().toJson(message)
            Log.e(TAG, "onEvent-textStr = $textStr")
            postMessage(message)
        }
    }

    fun onDestroy() {
        mNimHandlerThread?.quit()
        mNimHandlerThread = null
    }

}