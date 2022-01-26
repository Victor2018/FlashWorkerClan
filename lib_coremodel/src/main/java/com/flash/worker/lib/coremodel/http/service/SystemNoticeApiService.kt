package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.SystemNoticeParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.SystemNoticeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface SystemNoticeApiService {

    @GET(SystemNoticeApi.UNREAD_NUM)
    suspend fun fetchUnreadNum(
        @Header("X-TOKEN") token: String?): NetworkResponse<SystemNoticeUnreadReq, HttpError>

    @POST(SystemNoticeApi.MARK_READ)
    suspend fun markRead(
        @Header("X-TOKEN") token: String?): NetworkResponse<BaseReq, HttpError>

    @POST(SystemNoticeApi.NOTICE)
    suspend fun fetchNotice(
        @Header("X-TOKEN") token: String?,
        @Body body: SystemNoticeParm?): NetworkResponse<SystemNoticeReq, HttpError>

}