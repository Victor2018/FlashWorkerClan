package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.BindAliPayParm
import com.flash.worker.lib.coremodel.data.parm.OpenUserUnBindParm
import com.flash.worker.lib.coremodel.data.req.AliAuthReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.OpenUserInfoReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.OpenUserApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OpenUserApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface OpenUserApiService {

    @POST(OpenUserApi.BIND_ALI_PAY)
    suspend fun bindAliPay(
            @Header("X-TOKEN") token: String?,
            @Body body: BindAliPayParm?): NetworkResponse<BaseReq, HttpError>

    @GET(OpenUserApi.ALI_AUTH_INFO)
    suspend fun fetchAliAuthInfo (
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<AliAuthReq, HttpError>

    @GET(OpenUserApi.OPEN_USER_INFO)
    suspend fun fetchOpenUserInfo (
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<OpenUserInfoReq, HttpError>

    @POST(OpenUserApi.OPEN_USER_UNBIND)
    suspend fun openUserUnBind (
            @Header("X-TOKEN") token: String?,
            @Body body: OpenUserUnBindParm?): NetworkResponse<BaseReq, HttpError>
}