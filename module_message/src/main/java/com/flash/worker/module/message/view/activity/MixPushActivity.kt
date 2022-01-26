package com.flash.worker.module.message.view.activity

import android.app.Activity
import android.os.Bundle
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.push.NimMixPushMessageHandler
import com.flash.worker.lib.common.util.Loger
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MixPushActivity
 * Author: Victor
 * Date: 2021/3/11 19:12
 * Description: 
 * -----------------------------------------------------------------
 */
class MixPushActivity: Activity() {
    private val TAG = "MixPushActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseIntent()
        finish()
    }

    fun parseIntent() {
        val intent = intent ?: return
        val uri = intent.data
        val bundle = intent.extras
        val map: HashMap<String, String>
        if (uri != null) {
            val parameterSet = uri.queryParameterNames
            map = HashMap((parameterSet.size shl 2) / 3 + 1)
            var value: String?
            for (p in parameterSet) {
                value = uri.getQueryParameter(p)
                if (value == null) {
                    continue
                }
                map[p] = value
            }
        } else if (bundle != null) {
            map = HashMap((bundle.size() shl 2) / 3 + 1)
            for (key in bundle.keySet()) {
                val valueObj = bundle[key]
                map.set(key,valueObj?.toString()!!)
            }
        } else {
            map = HashMap(0)
        }
        val isMainActivityLaunched = App.get().isMainActivityLaunched()
        Loger.e(TAG,"isMainActivityLaunched = $isMainActivityLaunched")
        if (!isMainActivityLaunched) {
            NimMixPushMessageHandler().onNotificationClicked(this.applicationContext, map)
        }

    }
}