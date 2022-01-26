package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.HotKeywordParm
import com.flash.worker.lib.coremodel.data.parm.SearchEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTaskParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.HomeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HomeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface HomeApiService {

    @POST(HomeApi.SEARCH_TALENT_RELEASE)
    suspend fun searchTalentRelease(
            @Body body: SearchTalentReleaseParm?)
            : NetworkResponse<SearchTalentReleaseReq, HttpError>

    @POST(HomeApi.SEARCH_EMPLOYER_RELEASE)
    suspend fun searchEmployerRelease(
            @Body body: SearchEmployerReleaseParm?)
            : NetworkResponse<SearchEmployerReleaseReq, HttpError>

    @GET(HomeApi.HOME_TALENT_DETAIL)
    suspend fun fetchHomeTalentDetail(
            @Header("X-TOKEN") token: String?,
            @Query("releaseId") releaseId: String?
    ): NetworkResponse<HomeTalentDetailReq, HttpError>

    @GET(HomeApi.HOME_EMPLOYER_DETAIL)
    suspend fun fetchHomeEmployerDetail(
            @Header("X-TOKEN") token: String?,
            @Query("releaseId") releaseId: String?
    ): NetworkResponse<HomeEmployerDetailReq, HttpError>

    @POST(HomeApi.HOT_KEYWORD)
    suspend fun fetchHotKeyword(
            @Header("X-TOKEN") token: String?,
            @Body body: HotKeywordParm?
    ): NetworkResponse<HotKeywordReq, HttpError>

    @GET(HomeApi.BANNER)
    suspend fun fetchBanner(@Query("bannerType") bannerType: Int
    ): NetworkResponse<BannerReq, HttpError>

    @GET(HomeApi.ANNOUNCEMENT)
    suspend fun fetchAnnouncement()
    : NetworkResponse<AnnouncementReq, HttpError>

    @POST(HomeApi.SEARCH_TASK)
    suspend fun searchTask(
        @Header("X-TOKEN") token: String?,
        @Body body: SearchTaskParm?)
    : NetworkResponse<SearchTaskReq, HttpError>

    @GET(HomeApi.TASK_DETAIL)
    suspend fun fetchTaskDetail(
        @Header("X-TOKEN") token: String?,
        @Query("releaseId") releaseId: String?
    ): NetworkResponse<TaskDetailReq, HttpError>


}