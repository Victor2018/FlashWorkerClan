package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WechatLoginData
 * Author: Victor
 * Date: 2021/1/12 15:03
 * Description: 
 * -----------------------------------------------------------------
 */
class WechatLoginData {
    var token: String? = null
    var userId: String? = null
    var openId: String? = null//这个不为空进入绑定手机号页面
}