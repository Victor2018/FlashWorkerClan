package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LoginEvent
 * Author: Victor
 * Date: 2021/7/14 17:24
 * Description: 
 * -----------------------------------------------------------------
 */
object LoginEvent {
    /**
     * 一键登录
     */
    const val one_key_login = "one_key_login"

    /**
     * 验证码登录
     */
    const val phone_code_login = "phone_code_login"

    /**
     * 微信登录
     */
    const val weichat_login = "weichat_login"
}