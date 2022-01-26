package com.flash.worker.module.login.util


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AliPhoneAuthutil
 * Author: Victor
 * Date: 2020/12/7 15:45
 * Description: 
 * -----------------------------------------------------------------
 */
object AliPhoneAuthutil {
    val TYPES = arrayOf(
        "全屏（竖屏）", "全屏（横屏）", "弹窗（竖屏）",
        "弹窗（横屏）", "底部弹窗", "自定义View", "自定义View（Xml）"
    )

    /**
     * 全屏（竖屏）
     */
    const val FULL_PORT = 0

    /**
     * 全屏（横屏）
     */
    const val FULL_LAND = 1

    /**
     * 弹窗（竖屏）
     */
    const val DIALOG_PORT = 2

    /**
     * "弹窗（横屏）
     */
    const val DIALOG_LAND = 3

    /**
     * 底部弹窗
     */
    const val DIALOG_BOTTOM = 4

    /**
     * 自定义View
     */
    const val CUSTOM_VIEW = 5

    /**
     * 自定义View（Xml）
     */
    const val CUSTOM_XML = 6

    const val THEME_KEY = "theme"

    const val LOGIN_TYPE = "login_type"
    const val LOGIN = 1
    const val LOGIN_DELAY = 2
}