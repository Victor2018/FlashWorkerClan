package com.flash.worker.lib.common.push

import android.content.Context
import android.text.TextUtils
import android.util.Log
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.JPushMessage
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.MainHandler
import com.flash.worker.lib.common.util.SharedPreferencesUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TagAliasOperatorHelper
 * Author: Victor
 * Date: 2021/9/16 15:44
 * Description: 
 * -----------------------------------------------------------------
 */
class TagAliasOperatorHelper {
    private val TAG = "TagAliasOperatorHelper"

    private var context: Context? = null

    private object Holder {val instance = TagAliasOperatorHelper() }

    companion object {
        val  instance: TagAliasOperatorHelper by lazy { Holder.instance }
    }

    fun init(context: Context?) {
        if (context != null) {
            this.context = context.applicationContext
        }
    }

    fun onTagOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage
    ) {
        val sequence = jPushMessage.sequence
        Loger.e(TAG,"action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.tags)
        Loger.e(TAG, "tags size:" + jPushMessage.tags.size)
        init(context)
        if (jPushMessage.errorCode == 0) {
            Loger.e(TAG, "action - modify tag Success,sequence:$sequence")
        } else {
            var logs = "Failed to modify tags"
            if (jPushMessage.errorCode == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean"
            }
            logs += ", errorCode:" + jPushMessage.errorCode
            Loger.e(TAG, logs)
        }
    }

    fun onCheckTagOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage
    ) {
        val sequence = jPushMessage.sequence
        Loger.e(TAG,"action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.checkTag)
        init(context)
        if (jPushMessage.errorCode == 0) {
            val logs = "modify tag " + jPushMessage.checkTag + " bind state success,state:" + jPushMessage.tagCheckStateResult
            Loger.e(TAG, logs)
        } else {
            val logs = "Failed to modify tags, errorCode:" + jPushMessage.errorCode
            Loger.e(TAG, logs)
        }
    }

    fun onAliasOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage
    ) {
        val sequence = jPushMessage.sequence
        Loger.e(TAG,"action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.alias)
        init(context)
        if (jPushMessage.errorCode == 0) {
            Loger.e(TAG, "action - modify alias Success,sequence:$sequence")
            SharedPreferencesUtils.setJPushAlias = true
        } else {
            val logs = "Failed to modify alias, errorCode:" + jPushMessage.errorCode
            Loger.e(TAG, logs)
            MainHandler.get().postDelayed({
                setJPushAlias()
            },20 * 1000)
        }
    }

    //设置手机号码回调
    fun onMobileNumberOperatorResult(
        context: Context?,
        jPushMessage: JPushMessage
    ) {
        val sequence = jPushMessage.sequence
        Loger.e(TAG,"action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.mobileNumber)
        init(context)
        if (jPushMessage.errorCode == 0) {
            Log.i(TAG, "action - set mobile number Success,sequence:$sequence")
        } else {
            val logs = "Failed to set mobile number, errorCode:" + jPushMessage.errorCode
            Loger.e(TAG, logs)
        }
    }

    fun setJPushAlias() {
        Loger.e(TAG,"setJPushAlias()......")
        if (!App.get().hasLogin()) {
            Loger.e(TAG,"setJPushAlias()......not login")
            return
        }
        var userInfo = App.get().getUserInfo()
        val alias = userInfo?.audienceAlias
        Loger.e(TAG,"setJPushAlias()-alias = $alias")
        if (TextUtils.isEmpty(alias)) return

        var setJPushAlias = SharedPreferencesUtils.setJPushAlias
        if (setJPushAlias) {
            Loger.e(TAG,"setJPushAlias()......has set success")
            return
        }

        var currentTimeMillis = System.currentTimeMillis().toInt()
        JPushInterface.setAlias(App.get(),currentTimeMillis++,alias)
    }

    fun deleteAlias () {
        Loger.e(TAG,"deleteAlias()......")
        var currentTimeMillis = System.currentTimeMillis().toInt()
        JPushInterface.deleteAlias(App.get(),currentTimeMillis++)
        SharedPreferencesUtils.setJPushAlias = false
    }

}