package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.CommonApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommonApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface CommonApiService {

    @POST(CommonApi.AREA_TREE)
    suspend fun getAreaTree(
            @Body body: AreaTreeParm?)
            : NetworkResponse<AreaTreeReq, HttpError>

    @GET(CommonApi.JOB_CATEGORY_TREE)
    suspend fun getTalentType(): NetworkResponse<TalentTypeReq, HttpError>

    @GET(CommonApi.CITY_AREA)
    suspend fun fetchCityArea(
            @Header("X-TOKEN") token: String?,
            @Query("city") city: String?
    ): NetworkResponse<CityAreaReq, HttpError>

    @GET(CommonApi.SYSTEM_TIME)
    suspend fun fetchSystemTime(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<SystemTimeReq, HttpError>

    @GET(CommonApi.REWARD_LABEL)
    suspend fun fetchRewardLabel(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<RewardLabelReq, HttpError>

    @GET(CommonApi.TALENT_VIOLATION)
    suspend fun fetchTalentViolationLabel(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<ViolationLabelReq, HttpError>

    @GET(CommonApi.EMPLOYER_VIOLATION)
    suspend fun fetchEmployerViolationLabel(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<ViolationLabelReq, HttpError>
}