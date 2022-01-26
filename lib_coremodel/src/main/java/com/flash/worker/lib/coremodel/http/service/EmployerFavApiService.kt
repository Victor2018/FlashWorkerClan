package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmployerFavApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFavApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmployerFavApiService {

    @POST(EmployerFavApi.ADD_FAV_RELEASE)
    suspend fun addFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerAddFavReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerFavApi.CANCEL_FAV_RELEASE)
    suspend fun cancelFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerCancelFavReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerFavApi.FAV_RELEASE)
    suspend fun fetchFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerFavReleaseParm?): NetworkResponse<EmployerFavReleaseReq, HttpError>


}