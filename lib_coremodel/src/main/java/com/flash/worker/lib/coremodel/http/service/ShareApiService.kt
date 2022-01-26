package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.ShareGuidInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareTaskInfoParm
import com.flash.worker.lib.coremodel.data.req.ShareInfoReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.ShareApi
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareApiService
 * Author: Victor
 * Date: 2021/8/19 17:08
 * Description: 
 * -----------------------------------------------------------------
 */
interface ShareApiService {
    @POST(ShareApi.SHARE_INFO)
    suspend fun fetchShareInfo(
        @Header("X-TOKEN") token: String?,
        @Body body: ShareInfoParm?): NetworkResponse<ShareInfoReq, HttpError>

    @POST(ShareApi.SHARE_GUILD_INFO)
    suspend fun fetchShareGuildInfo(
        @Header("X-TOKEN") token: String?,
        @Body body: ShareGuidInfoParm?): NetworkResponse<ShareInfoReq, HttpError>

    @POST(ShareApi.SHARE_TASK_INFO)
    suspend fun fetchShareTaskInfo(
        @Header("X-TOKEN") token: String?,
        @Body body: ShareTaskInfoParm?): NetworkResponse<ShareInfoReq, HttpError>
}