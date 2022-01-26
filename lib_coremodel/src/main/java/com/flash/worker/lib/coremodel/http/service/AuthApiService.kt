package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.AuthApi
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.BindPhoneReq
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.data.req.WechatLoginReq
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface AuthApiService {

    @POST(AuthApi.SEND_SMS)
    suspend fun getSms(@Body body: SmsParm): NetworkResponse<BaseReq, HttpError>

    @POST(AuthApi.SEND_BIND_PHONE_SMS)
    suspend fun getBindPhoneSms(@Body body: SmsParm): NetworkResponse<BaseReq, HttpError>

    @POST(AuthApi.SMS_LOGIN)
    suspend fun smsLogin(@Body body: SmsLoginParm): NetworkResponse<LoginReq, HttpError>

    @POST(AuthApi.LOG_OUT)
    suspend fun logOut(@Header("X-TOKEN") token: String?): NetworkResponse<BaseReq, HttpError>

    @POST(AuthApi.BIND_PHONE)
    suspend fun bindPhone(@Body body: BindPhoneParm):
            NetworkResponse<BindPhoneReq, HttpError>

    @POST(AuthApi.ONE_PASS_LOGIN)
    suspend fun oneKeyLogin(@Body body: OneKeyLoginParm):
            NetworkResponse<LoginReq, HttpError>

    @POST(AuthApi.WECHAT_LOGIN)
    suspend fun wechatLogin(@Body body: WechatLoginParm):
            NetworkResponse<WechatLoginReq, HttpError>

    @POST(AuthApi.SEND_VERIFY_SMS)
    suspend fun sendVerifySms (
            @Header("X-TOKEN") token: String?): NetworkResponse<BaseReq, HttpError>

}