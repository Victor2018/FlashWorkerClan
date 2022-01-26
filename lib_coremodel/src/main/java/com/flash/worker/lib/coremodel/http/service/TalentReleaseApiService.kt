package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.TalentReleaseApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface TalentReleaseApiService {

    @POST(TalentReleaseApi.SAVE_TALENT_DRAFTS)
    suspend fun saveTalentDrafts(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveTalentReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.SAVE_TALENT_RELEASE)
    suspend fun saveTalentRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveTalentReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.UPDATE_TALENT_DRAFTS)
    suspend fun updateTalentDrafts(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateTalentReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.UPDATE_TALENT_RELEASE)
    suspend fun updateTalentRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateTalentReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.TALENT_RELEASE_LIST)
    suspend fun getTalentReleaseList(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentReleaseParm?): NetworkResponse<TalentReleaseReq, HttpError>

    @POST(TalentReleaseApi.TALENT_RELEASE_REFRESH)
    suspend fun talentReleaseRefresh(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentReleaseRefreshParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.TALENT_RELEASE_OFFSHELF)
    suspend fun talentReleaseOffShelf(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentReleaseOffShelfParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentReleaseApi.TALENT_RELEASE_NEW_RELEASE)
    suspend fun talentReleaseNewRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentReleaseNewReleaseParm?): NetworkResponse<BaseReq, HttpError>


    @POST(TalentReleaseApi.TALENT_RELEASE_DELETE)
    suspend fun talentReleaseDelete(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentReleaseDeleteParm?): NetworkResponse<BaseReq, HttpError>

    @GET(TalentReleaseApi.TALENT_RELEASE_DETAIL)
    suspend fun getTalentReleaseDetail(
            @Header("X-TOKEN") token: String?,
            @Query("releaseId") releaseId : String?): NetworkResponse<TalentReleaseDetailReq, HttpError>
}