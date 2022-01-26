package com.flash.worker.lib.common.app

import android.app.Activity
import android.app.Application
import android.app.Notification
import android.os.Bundle
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.flash.worker.lib.common.base.BaseApplication
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.push.NimMixPushMessageHandler
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.ProvinceInfo
import com.flash.worker.lib.coremodel.data.bean.UserInfo
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.common.util.SharedPreferencesUtils
import com.flash.worker.lib.coremodel.data.bean.ImLoginInfo
import com.flash.worker.lib.im.NimMessageManager
import com.heytap.msp.push.HeytapPushManager
import com.huawei.hms.support.common.ActivityMgr
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.mixpush.NIMPushClient
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.tauth.Tencent
import com.victor.crash.library.SpiderCrashHandler
import java.lang.ref.WeakReference

import cn.jpush.android.api.BasicPushNotificationBuilder
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.push.JPushOpenHelper


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: App
 * Author: Victor
 * Date: 2020/11/27 14:53
 * Description: 
 * -----------------------------------------------------------------
 */
class App: BaseApplication (), Application.ActivityLifecycleCallbacks {
    val TAG = "App"

    //为避免内存泄漏使用弱引用
    var mCurrentActivity: WeakReference<Activity>? = null
    var allActivitys = ArrayList<Activity>()
    var mLoginReq: LoginReq? = null
    var mUserInfo: UserInfo? = null
    var mProvinceDatas:List<ProvinceInfo>? = null
    val defaultProvince = "广东省"
    val defaultCity = "深圳市"
    var mProvince: String = defaultProvince
    var mCity: String = defaultCity
    var mGuildCity: String = defaultCity
    var isSelectCity = false
    var isSelectGuildCity = false
    var hasShowChangeCity = false//是否已显示切换定位城市弹窗
    var hasShowGPSNotOpenTip = false//是否已显示定位服务未开启弹窗

    var isLocationPermissionDeclined: Boolean = false//是否拒绝定位权限
    var isLocationPermissionPermanentlyDenied: Boolean = false//是否永久拒绝定位权限

    companion object {
        private lateinit var instance : App
        fun get() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        registerActivityLifecycleCallbacks(this)
        //qq分享初始化
        Tencent.setIsPermissionGranted(true)
        //crash spider
        SpiderCrashHandler.init(this)
        //bugly
        CrashReport.initCrashReport(this)
        //云信初始化
        NIMClient.init(this, null, NimSDKOptionConfig.getSDKOptions(this))
        //初始化主进程
        if (AppUtil.inMainProcess(this)) {

            //云信初始化
            NimMessageManager.instance.init()

            //华为推送初始化
            ActivityMgr.INST.init(this)

            //初始化OPPO PUSH服务，创建默认通道
            HeytapPushManager.init(this, true)

            // 注册自定义推送消息处理，这个是可选项
            NIMPushClient.registerMixPushMessageHandler(NimMixPushMessageHandler())

            // 初始化消息提醒
            NIMClient.toggleNotification(true)
        }
        UMengEventModule.preInitSdk(this)
    }

    fun setLoginReq(loginReq: LoginReq?) {
        mLoginReq = loginReq
        SharedPreferencesUtils.loginReq = JsonUtils.toJSONString(loginReq)
    }

    fun getLoginReq(): LoginReq? {
        val logRes = SharedPreferencesUtils.loginReq ?: ""
        if (!TextUtils.isEmpty(logRes)) {
            mLoginReq = JsonUtils.parseObject(logRes, LoginReq::class.java)
            return mLoginReq
        }
        return null
    }

    fun setUserInfo(userInfo: UserInfo?) {
        mUserInfo = userInfo
        SharedPreferencesUtils.userInfo = JsonUtils.toJSONString(mUserInfo)
    }

    fun getUserInfo(): UserInfo? {
        val userRes = SharedPreferencesUtils.userInfo
        if (!TextUtils.isEmpty(userRes)) {
            mUserInfo = JsonUtils.parseObject(userRes, UserInfo::class.java)
            return mUserInfo
        }
        return null
    }

    fun setCityData(provinceData: List<ProvinceInfo>?) {
        mProvinceDatas = provinceData
        SharedPreferencesUtils.cityDatas = JsonUtils.toJSONString(mProvinceDatas)
    }

    fun getCityData(): List<ProvinceInfo>? {
        val provinceRes = SharedPreferencesUtils.cityDatas
        if (!TextUtils.isEmpty(provinceRes)) {
            mProvinceDatas = JsonUtils.parseArray(provinceRes, ProvinceInfo::class.java)
            return mProvinceDatas
        }
        return null
    }

    fun setCity (city: String?) {
        if (TextUtils.isEmpty(city)) {
            mCity = defaultCity
            return
        }
        mCity = city ?: defaultCity
    }

    fun setGuildCity (city: String?) {
        if (TextUtils.isEmpty(city)) {
            mGuildCity = defaultCity
            return
        }
        mGuildCity = city ?: defaultCity
    }

    fun hasLogin(): Boolean {
        if (getLoginReq() == null) return false
        return true
    }

    fun hasLocation (): Boolean {
        return AmapLocationUtil.instance.mLocation != null
    }

    fun resetLocation () {
        isSelectCity = false
        mProvince = defaultProvince
        mCity = defaultCity
        AmapLocationUtil.instance.mLocation = null
    }

    fun resetLoginData () {
        Loger.e(TAG,"resetLoginData()......")
        setLoginReq(null)
        setUserInfo(null)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        allActivitys.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (!allActivitys.contains(activity)) {
            allActivitys.add(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Loger.e(TAG,"onActivityResumed()...simpleName = " + activity.javaClass.simpleName)
        mCurrentActivity = WeakReference(activity)
    }

    /**
     * 判断当前页面是不是主界面
     */
    fun isCurrentActivityMain (): Boolean {
        val simpleName = mCurrentActivity?.get()?.javaClass?.simpleName
        return TextUtils.equals("MainActivity",simpleName)
    }

    /**
     * 判断主界面是否已启动
     */
    fun isMainActivityLaunched (): Boolean {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (TextUtils.equals("MainActivity",simpleName)) return true
        }
        return false
    }

    /**
     * 关闭SplashActivity,MainActivity 以为的activity
     */
    fun finishOtherThenSplashAndMainActivity () {
        allActivitys.forEach {
            val simpleName = it.javaClass.simpleName
            if (!TextUtils.equals("MainActivity",simpleName) &&
                !TextUtils.equals("SplashActivity",simpleName)) {
                it.finish()
            }
        }
    }

    fun finishAllActivity () {
        allActivitys.forEach {
            it.finish()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        allActivitys.clear()
        mCurrentActivity = null
        mLoginReq = null
        mUserInfo = null
        mProvinceDatas = null
    }
}