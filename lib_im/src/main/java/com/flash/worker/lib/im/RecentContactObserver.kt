package com.flash.worker.lib.im

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.google.gson.Gson


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecentContactObserver
 * Author: Victor
 * Date: 2020/12/30 11:01
 * Description: 
 * -----------------------------------------------------------------
 */
class RecentContactObserver: Observer<List<RecentContact>> {
    val TAG = "RecentContactObserver"

    private var mNimHandler: Handler? = null
    private var mNimHandlerThread: HandlerThread? = null

    init {
        startRecentContactTask()
    }

    private fun startRecentContactTask() {
        mNimHandlerThread = HandlerThread("recentContactTask")
        mNimHandlerThread?.start()
        mNimHandler = object : Handler(mNimHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {
                dispatchRecentContact(msg.obj as RecentContact)
            }
        }
    }

    protected fun dispatchRecentContact(recentContact: RecentContact?) {
        try {
            if (recentContact == null) return
            LiveDataBus.send(IMActions.NEW_RECENT_CONTACT,recentContact)
            LiveDataBus.send(IMActions.REFRESH_UNREAD_COUNT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    protected fun postMessage(recentContact: RecentContact?) {
        if (mNimHandler != null) {
            val msg: Message = mNimHandler?.obtainMessage()!!
            msg.obj = recentContact
            mNimHandler?.sendMessage(msg)
        }
    }

    override fun onEvent(recentContacts: List<RecentContact>?) {
        if (recentContacts == null) {
            return
        }
        for (recentContact in recentContacts) {
            val textStr: String = Gson().toJson(recentContact)
            Log.e(TAG, "onEvent-textStr = $textStr")
            postMessage(recentContact)
        }
    }

    fun onDestroy() {
        mNimHandlerThread?.quit()
        mNimHandlerThread = null
    }

}