package com.flash.worker.lib.common.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.push.JPushOpenHelper
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.util.AppConfig
import kotlinx.android.synthetic.main.activity_jpush.*
import org.json.JSONException
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JPushOpenActivity
 * Author: Victor
 * Date: 2021/9/17 12:04
 * Description: 
 * -----------------------------------------------------------------
 */
class JPushOpenActivity: AppCompatActivity() {
    private val TAG = "JPushOpenActivity"

    /**消息Id */
    private val KEY_MSGID = "msg_id"

    /**该通知的下发通道 */
    private val KEY_WHICH_PUSH_SDK = "rom_type"

    /**通知标题 */
    private val KEY_TITLE = "n_title"

    /**通知内容 */
    private val KEY_CONTENT = "n_content"

    /**通知附加字段 */
    private val KEY_EXTRAS = "n_extras"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jpush)
        handleOpenClick()
        finish()
    }

    /**
     * 处理点击事件，当前启动配置的Activity都是使用
     * Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
     * 方式启动，只需要在onCreat中调用此方法进行处理
     */
    private fun handleOpenClick() {
        Log.e(TAG, "handleOpenClick-用户点击打开了通知")
        var data: String? = null
        //获取华为平台附带的jpush信息
        if (intent.data != null) {
            data = intent.data.toString()
        }

        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(data) && intent.extras != null) {
            data = intent.extras?.getString("JMessageExtra")
        }
        Loger.e(TAG, "msg content is " + data.toString())
        if (TextUtils.isEmpty(data)) return

        try {
            val jsonObject = JSONObject(data)
            val msgId = jsonObject.optString(KEY_MSGID)
            val whichPushSDK = jsonObject.optInt(KEY_WHICH_PUSH_SDK)
            val title = jsonObject.optString(KEY_TITLE)
            val content = jsonObject.optString(KEY_CONTENT)
            val extras = jsonObject.optString(KEY_EXTRAS)

            val sb = StringBuilder()
            sb.append("msgId:")
            sb.append(msgId.toString())
            sb.append("\n")
            sb.append("title:")
            sb.append(title.toString())
            sb.append("\n")
            sb.append("content:")
            sb.append(content.toString())
            sb.append("\n")
            sb.append("extras:")
            sb.append(extras.toString())
            sb.append("\n")
            sb.append("platform:")
            sb.append(getPushSDKName(whichPushSDK))
            mTvStatus.text = sb.toString()

            //上报点击事件
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK.toByte(), data)

            if (App.get().isMainActivityLaunched()) {
                //如果主界面已打开直接处理极光推送跳转
                JPushOpenHelper.parseIntentAction(this,extras)
            } else {
                SplashActivity.intentStart(this,extras)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Loger.e(TAG, "parse notification error")
        }
    }

    private fun getPushSDKName(whichPushSDK: Int): String? {
        val name: String
        name = when (whichPushSDK) {
            0 -> "jpush"
            1 -> "xiaomi"
            2 -> "huawei"
            3 -> "meizu"
            4 -> "oppo"
            5 -> "vivo"
            6 -> "asus"
            8 -> "fcm"
            else -> "jpush"
        }
        return name
    }
}