package com.flash.worker.lib.livedatabus.action


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LoginActions
 * Author: Victor
 * Date: 2020/12/11 17:11
 * Description: 
 * -----------------------------------------------------------------
 */
object LoginActions {
    /**
     * 微信第三方登录
     */
    const val LOGIN_WX_AUTH_ERROR = "LOGIN_WX_AUTH_ERROR"

    /**
     * 微信第三方登录
     */
    const val LOGIN_AUTH_WEIXIN = "LOGIN_AUTH_WEIXIN"

    /**
     * 启动微信登录
     */
    const val GO_WECHAT_LOGIN = "GO_WECHAT_LOGIN"

    /**
     * 启动一键登录
     */
    const val GO_ONE_KEY_LOGIN = "GO_ONE_KEY_LOGIN"

    /**
     * 登录成功
     */
    const val LOGIN_SUCCESS = "LOGIN_SUCCESS"

    /**
     * 登录云信
     */
    const val LOGIN_NIM = "LOGIN_NIM"

    /**
     * token失效
     */
    const val TOKEN_INVALID = "TOKEN_INVALID"

}