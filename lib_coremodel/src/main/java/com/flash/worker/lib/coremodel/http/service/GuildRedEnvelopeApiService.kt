package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.GuildRedEnvelopeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface GuildRedEnvelopeApiService {

    @GET(GuildRedEnvelopeApi.RED_ENVELOPE_INFO)
    suspend fun fetchRedEnvelopeInfo(
            @Header("X-TOKEN") token: String?): NetworkResponse<GuildRedEnvelopeInfoReq, HttpError>

    @POST(GuildRedEnvelopeApi.RECEIVE_RED_ENVELOPE)
    suspend fun receiveRedEnvelope(
            @Header("X-TOKEN") token: String?): NetworkResponse<BaseReq, HttpError>

    @GET(GuildRedEnvelopeApi.GUILD_RED_ENVELOPE_TIPS)
    suspend fun guildRedEnvelopeTips(
            @Header("X-TOKEN") token: String?): NetworkResponse<GuildRedEnvelopeTipsReq, HttpError>


}