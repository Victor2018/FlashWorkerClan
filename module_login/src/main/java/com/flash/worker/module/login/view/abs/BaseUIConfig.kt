package com.flash.worker.module.login.view.abs

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.util.TypedValue
import android.view.Surface
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.module.login.R
import com.flash.worker.module.login.util.AliPhoneAuthutil
import com.flash.worker.module.login.view.widget.CustomXmlConfig
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BaseUIConfig
 * Author: Victor
 * Date: 2020/12/7 15:43
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class BaseUIConfig(var activity: Activity?,authHelper: PhoneNumberAuthHelper?) {
    var mScreenWidthDp = 0
    var mScreenHeightDp = 0
    var canBack: Boolean = true

    companion object {
        fun init(
            type: Int,
            activity: Activity?,
            authHelper: PhoneNumberAuthHelper?
        ): BaseUIConfig? {
            return when (type) {
//                AliPhoneAuthutil.FULL_PORT -> FullPortConfig(activity, authHelper)
//            AliPhoneAuthutil.FULL_LAND -> FullLandConfig(activity, authHelper)
//            AliPhoneAuthutil.DIALOG_PORT -> DialogPortConfig(activity, authHelper)
//            AliPhoneAuthutil.DIALOG_LAND -> DialogLandConfig(activity, authHelper)
//            AliPhoneAuthutil.DIALOG_BOTTOM -> DialogBottomConfig(activity, authHelper)
//            AliPhoneAuthutil.CUSTOM_VIEW -> CustomViewConfig(activity, authHelper)
            AliPhoneAuthutil.CUSTOM_XML -> CustomXmlConfig(activity, authHelper)
                else -> null
            }
        }
    }

    protected fun initSwitchView(marginTop: Int): View? {
        val switchTV = TextView(activity)
        val mLayoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                DensityUtil.dip2px(activity!!, 50f)
            )
        //一键登录按钮默认marginTop 270dp
        mLayoutParams.setMargins(0, DensityUtil.dip2px(activity!!, marginTop.toFloat()), 0, 0)
        mLayoutParams.addRule(
            RelativeLayout.CENTER_HORIZONTAL,
            RelativeLayout.TRUE
        )
        switchTV.setText(R.string.login_switch_msg)
        switchTV.setTextColor(Color.BLACK)
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.0f)
        switchTV.layoutParams = mLayoutParams
        return switchTV
    }

    protected fun updateScreenSize(authPageScreenOrientation: Int) {
        var authPageScreenOrientation = authPageScreenOrientation
        val screenHeightDp: Int = DensityUtil.px2dip(activity!!, DensityUtil.getDisplayHeight().toFloat())
        val screenWidthDp: Int = DensityUtil.px2dip(activity!!, DensityUtil.getDisplayWidth().toFloat())
        var rotation = activity!!.windowManager.defaultDisplay.rotation
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_BEHIND) {
            authPageScreenOrientation = activity!!.requestedOrientation
        }
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        ) {
            rotation = Surface.ROTATION_90
        } else if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        ) {
            rotation = Surface.ROTATION_180
        }
        when (rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                mScreenWidthDp = screenWidthDp
                mScreenHeightDp = screenHeightDp
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                mScreenWidthDp = screenHeightDp
                mScreenHeightDp = screenWidthDp
            }
            else -> {
            }
        }
    }

    abstract fun configAuthPage()

    /**
     * 在横屏APP弹竖屏一键登录页面或者竖屏APP弹横屏授权页时处理特殊逻辑
     * Android8.0只能启动SCREEN_ORIENTATION_BEHIND模式的Activity
     */
    fun onResume() {}
}