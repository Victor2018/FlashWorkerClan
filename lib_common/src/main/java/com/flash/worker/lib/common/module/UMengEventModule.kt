package com.flash.worker.lib.common.module

import android.content.Context
import com.flash.worker.lib.coremodel.util.AppConfig
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UMengEventModule
 * Author: Victor
 * Date: 2021/7/14 17:04
 * Description: UMeng 时间上报模块 release 线上版本才上报
 * -----------------------------------------------------------------
 */
object UMengEventModule {

    val canReport = !AppConfig.MODEL_DEBUG && AppConfig.MODEL_ONLINE

    fun preInitSdk (context: Context?) {
        if (!canReport) return
        UMConfigure.preInit(context, AppConfig.UMENG_APP_KEY, AppConfig.UMENG_CHANNEL)
    }

    fun initSdk(context: Context?) {
        if (!canReport) return
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(AppConfig.MODEL_DEBUG)
        UMConfigure.init(context,AppConfig.UMENG_APP_KEY, AppConfig.UMENG_CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,null)

        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true) //支持多进程打点

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    fun report (context: Context?,eventID: String?) {
        if (!canReport) return
        MobclickAgent.onEvent(context,eventID)
    }

    fun report (context: Context?,eventID: String?,param: String?) {
        if (!canReport) return
        MobclickAgent.onEvent(context,eventID,param)
    }

}