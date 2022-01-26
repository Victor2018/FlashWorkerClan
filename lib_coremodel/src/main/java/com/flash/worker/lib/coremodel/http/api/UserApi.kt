package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserApi
 * Author: Victor
 * Date: 2021/4/12 20:02
 * Description: 
 * -----------------------------------------------------------------
 */
object UserApi {
    const val GET_USER_INFO = "user/getUserInfo"
    const val UPDATE_USER_AVATAR = "user/headpic"
    const val UPDATE_LIVE_CIGY = "user/liveCity"
    const val UPDATE_USER_NAME = "user/username"
    const val UPDATE_WORK_YEARS = "user/workYears"
    const val UPDATE_IDENTITY = "user/identity"
    const val UPDATE_HEIGHT = "user/height"
    const val UPDATE_WEIGHT = "user/weight"
    const val UPDATE_EMERGENCY_CONTACT = "user/contactPhone"
    const val AUTH_REAL = "user/authRealName"
    const val CHECK_TALENT_BASE_INFO = "user/checkTalentBaseInfo"
    const val UPDATE_TALENT_BASE_INFO = "user/updateTalentBaseInfo"
    const val CHECK_EMPLOYER_BASE_INFO = "user/checkEmployerBaseInfo"
    const val UPDATE_EMPLOYER_BASE_INFO = "user/updateEmployerBaseInfo"
    const val IM_LOGIN_INFO = "user/getImUserInfo"
    const val IM_USER_INFO = "user/getUserInfoByImAccid"
    const val CHECK_CANCEL_ACCOUNT = "user/checkCancelAccount"
    const val CANCEL_ACCOUNT_VERIFY = "user/cancelAccountSafeVerify"
    const val CANCEL_ACCOUNT = "user/cancelAccount"
    const val REAL_NAME = "user/manualAuthRealName"
    const val UPDATE_USER_INFO = "user/updateNewUserInfo"
    const val UPDATE_INVITE_USER = "user/updateInviterUser"

}