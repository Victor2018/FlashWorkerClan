package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.AccountApi
import com.flash.worker.lib.coremodel.http.api.TalentRedEnvelopeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentRedEnvelopeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface TalentRedEnvelopeApiService {

    @GET(TalentRedEnvelopeApi.ACTIVITY_INFO)
    suspend fun fetchActivityInfo(
            @Header("X-TOKEN") token: String?): NetworkResponse<ActivityInfoReq, HttpError>

    @POST(TalentRedEnvelopeApi.RECEIVE_RED_ENVELOPE)
    suspend fun receiveTalentRedEnvelope(
            @Header("X-TOKEN") token: String?,
            @Body body: ReceiveTalentRedEnvelopeParm?): NetworkResponse<BaseReq, HttpError>

    @GET(TalentRedEnvelopeApi.WAIT_RECEIVE_COUNT)
    suspend fun fetchWaitReceiveCount(
            @Header("X-TOKEN") token: String?): NetworkResponse<WaitReceiveCountReq, HttpError>

}