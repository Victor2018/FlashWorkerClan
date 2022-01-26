package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.TalentFavApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface TalentFavApiService {

    @POST(TalentFavApi.ADD_FAV_RELEASE)
    suspend fun addFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentAddFavReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentFavApi.CANCEL_FAV_RELEASE)
    suspend fun cancelFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentCancelFavReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentFavApi.FAV_RELEASE)
    suspend fun fetchFavRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentFavReleaseParm?): NetworkResponse<TalentFavReleaseReq, HttpError>


}