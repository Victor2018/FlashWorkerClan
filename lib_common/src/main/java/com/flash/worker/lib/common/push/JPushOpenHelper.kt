package com.flash.worker.lib.common.push

import android.app.Notification
import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.BasicPushNotificationBuilder
import cn.jpush.android.api.JPushInterface
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.MainHandler
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.lib.coremodel.util.AppConfig
import org.json.JSONObject


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JPushOpenHelper
 * Author: Victor
 * Date: 2021/9/17 15:51
 * Description: 极光推送跳转模块
 * -----------------------------------------------------------------
 */

object JPushOpenHelper {
    val TAG = "JPushOpenHelper"

    fun initJPush (context: Context) {
        Loger.e(TAG,"initJPush()......")
        val builder = BasicPushNotificationBuilder(context)
        builder.statusBarDrawable = R.mipmap.jpush_notification_icon
        builder.notificationFlags = (Notification.FLAG_AUTO_CANCEL
                or Notification.FLAG_SHOW_LIGHTS) //设置为自动消失和呼吸灯闪烁

        builder.notificationDefaults = (Notification.DEFAULT_SOUND
                or Notification.DEFAULT_VIBRATE
                or Notification.DEFAULT_LIGHTS) // 设置为铃声、震动、呼吸灯闪烁都要

        JPushInterface.setDefaultPushNotificationBuilder(builder)

        JPushInterface.setDebugMode(AppConfig.MODEL_DEBUG)
        JPushInterface.init(context)

        var jPushRegistrationID = JPushInterface.getRegistrationID(context)
        Loger.e(TAG,"jPushRegistrationID = $jPushRegistrationID")
    }

    fun parseIntentAction (activity: AppCompatActivity,extras: String?) {
        Loger.e(TAG,"parseIntentAction-extras = $extras")
        if (!App.get().hasLogin()) return
        MainHandler.get().post {
            if (TextUtils.isEmpty(extras)) return@post
            try {
                val intentData = JSONObject(extras)
                val type = intentData.optInt("type")
                when (type) {
                    10 -> {//收到邀请
                        val employerReleaseId = intentData.optString("employerReleaseId")
                        val talentReleaseId = intentData.optString("talentReleaseId")
                        val resumeId = intentData.optString("resumeId")
                        NavigationUtils.goJobDetailActivity(activity,employerReleaseId,talentReleaseId,resumeId,
                            JobDetailAction.ACCEPT_INVITATION)
                    }
                    15 -> {//报名提醒
                        NavigationUtils.goMyEmploymentActivity(activity,0)
                    }
                    20 -> {//雇用通知
                        NavigationUtils.goMyWorkActivity(activity,1)
                    }
                    25 -> {//雇用终止
                        val jobOrderId = intentData.optString("jobOrderId")
                        NavigationUtils.goTalentOrderDetailActivity(activity,jobOrderId)
                    }
                    35 -> {//雇主违约
                        NavigationUtils.goMyWorkActivity(activity,2)
                    }
                    40 -> {//人才违约
                        NavigationUtils.goMyEmploymentActivity(activity,1)
                    }
                    45 -> {//人才举报
                        val complaintNo = intentData.optString("complaintNo")
                        NavigationUtils.goEmployerHandlingDetailActivity(activity,complaintNo)
                    }
                    50 -> {//雇主投诉
                        val complaintNo = intentData.optString("complaintNo")
                        NavigationUtils.goTalentHandlingDetailActivity(activity,complaintNo)
                    }
                    80 -> {//到岗打卡提醒
                        NavigationUtils.goMyWorkActivity(activity,1)
                    }
                    85 -> {//完工打卡提醒
                        NavigationUtils.goMyWorkActivity(activity,1)
                    }
                    90 -> {//雇主已预付报酬
                        NavigationUtils.goMyWorkActivity(activity,1)
                    }
                    95 -> {//雇主已结算报酬
                        val outTradeNo = intentData.optString("outTradeNo")
                        val data = BalanceFlowInfo()
                        data.bizName = "报酬收入"
                        data.bizType = type
                        data.outTradeNo = outTradeNo
                        NavigationUtils.goBalanceFlowDetailActivity(activity,data)
                    }
                    100 -> {//预付提醒
                        NavigationUtils.goMyEmploymentActivity(activity,1)
                    }
                    105 -> {//结算提醒
                        NavigationUtils.goMyEmploymentActivity(activity,1)
                    }
                    120 -> {//任务收到邀请
                        val employerReleaseId = intentData.optString("employerReleaseId")
                        val talentReleaseId = intentData.optString("talentReleaseId")
                        val resumeId = intentData.optString("resumeId")
                        NavigationUtils.goTaskDetailActivity(activity,employerReleaseId,talentReleaseId,resumeId,
                            TaskDetailAction.ACCEPT_INVITATION)
                    }
                    125 -> {//任务结算提醒
                        NavigationUtils.goMyEmploymentActivity(activity,1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}