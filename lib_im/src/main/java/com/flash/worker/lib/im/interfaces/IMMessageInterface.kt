package com.flash.worker.lib.im.interfaces


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IMMessageInterface
 * Author: Victor
 * Date: 2020/12/29 12:06
 * Description: 
 * -----------------------------------------------------------------
 */
interface IMMessageInterface<T> {
    /**
     * 初始化
     */
    fun init()

    /**
     * 登录
     *
     * @param t
     * @param retry
     */
    fun login(t: T?)

    /**
     * 重试登录
     */
    fun retryLogin()

    /**
     * 登出
     */
    fun logout()

    /**
     * 是否已登录
     *
     * @return
     */
    fun hasLogin(): Boolean
}