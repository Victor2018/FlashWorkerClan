package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthApi
 * Author: Victor
 * Date: 2021/4/12 20:01
 * Description: 
 * -----------------------------------------------------------------
 */
object AuthApi {
    const val SEND_SMS = "auth/sendSms"
    const val SEND_BIND_PHONE_SMS = "auth/sendBindPhoneSms"
    const val SMS_LOGIN = "auth/smsLogin"
    const val LOG_OUT = "auth/logout"
    const val BIND_PHONE = "auth/bindPhoneLogin"
    const val ONE_PASS_LOGIN = "auth/onePassLogin"
    const val WECHAT_LOGIN = "auth/weixinLogin"
    const val SEND_VERIFY_SMS = "auth/sendVerifySms"
}