package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.PayParm
import com.flash.worker.lib.coremodel.data.parm.PayQueryParm
import com.flash.worker.lib.coremodel.data.req.PayQueryReq
import com.flash.worker.lib.coremodel.data.req.PayReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.PayApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface PayApiService {

    @POST(PayApi.PAY)
    suspend fun pay(
            @Header("X-TOKEN") token: String?,
            @Body body: PayParm?): NetworkResponse<PayReq, HttpError>

    @POST(PayApi.PAY_QUERY)
    suspend fun payQuery(
            @Header("X-TOKEN") token: String?,
            @Body body: PayQueryParm?): NetworkResponse<PayQueryReq, HttpError>


}