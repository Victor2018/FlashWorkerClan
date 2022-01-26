package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BindPhoneData
 * Author: Victor
 * Date: 2021/6/15 14:41
 * Description: 
 * -----------------------------------------------------------------
 */
class BindPhoneData: LoginData() {
    var genTime: String? = null//生成时间
    var openId: String? = null//第三方唯一ID
}