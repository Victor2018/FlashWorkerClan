package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.BindPhoneReq
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.data.req.WechatLoginReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IAuthDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IAuthDS {
    val smsData: LiveData<HttpResult<BaseReq>>
    suspend fun fetchSmsData(body: SmsParm)

    val bindPhoneSmsData: LiveData<HttpResult<BaseReq>>
    suspend fun fetchBindPhoneSmsData(body: SmsParm)

    val smsLoginData: LiveData<HttpResult<LoginReq>>
    suspend fun smsLogin(body: SmsLoginParm)

    val logOutData: LiveData<HttpResult<BaseReq>>
    suspend fun logOut(token: String?)

    val bindPhoneData: LiveData<HttpResult<BindPhoneReq>>
    suspend fun bindPhone(body: BindPhoneParm)

    val oneKeyLoginData: LiveData<HttpResult<LoginReq>>
    suspend fun oneKeyLogin(body: OneKeyLoginParm)

    val wechatLoginData: LiveData<HttpResult<WechatLoginReq>>
    suspend fun wechatLogin(body: WechatLoginParm)

    val verifySmsData: LiveData<HttpResult<BaseReq>>
    suspend fun sendVerifySms(token: String?)

}