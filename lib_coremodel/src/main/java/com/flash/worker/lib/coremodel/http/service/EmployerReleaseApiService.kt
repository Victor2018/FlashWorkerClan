package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseReq
import com.flash.worker.lib.coremodel.data.req.InviteTalentEmployerReleaseReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmployerReleaseApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmployerReleaseApiService {

    @POST(EmployerReleaseApi.SAVE_EMPLOYER_DRAFTS)
    suspend fun saveEmployerDrafts(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveEmployerReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.SAVE_EMPLOYER_RELEASE)
    suspend fun saveEmployerRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveEmployerReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.EMPLOYER_RELEASE_LIST)
    suspend fun fetchEmployerRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleaseParm?): NetworkResponse<EmployerReleaseReq, HttpError>

    @POST(EmployerReleaseApi.EMPLOYER_RELEASE_REFRESH)
    suspend fun employerReleaseRefresh(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleaseRefreshParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.EMPLOYER_RELEASE_OFFSHELF)
    suspend fun employerReleaseOffShelf(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleaseOffShelfParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.EMPLOYER_RELEASE_NEW_RELEASE)
    suspend fun employerReleaseNewRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleaseNewReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.EMPLOYER_RELEASE_DELETE)
    suspend fun employerReleaseDelete(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleaseDeleteParm?): NetworkResponse<BaseReq, HttpError>

    @GET(EmployerReleaseApi.EMPLOYER_RELEASE_DETAIL)
    suspend fun fetchEmployerReleaseDetail(
            @Header("X-TOKEN") token: String?,
            @Query("releaseId") releaseId : String?): NetworkResponse<EmployerReleaseDetailReq, HttpError>

    @POST(EmployerReleaseApi.UPDATE_EMPLOYER_DRAFTS)
    suspend fun updateEmployerDrafts(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveEmployerReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.UPDATE_EMPLOYER_RELEASE)
    suspend fun updateEmployerRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveEmployerReleaseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerReleaseApi.INVITE_TALENT_EMPLOYER_RELEASE)
    suspend fun fetchInviteTalentEmployerRelease(
            @Header("X-TOKEN") token: String?,
            @Body body: InviteTalentEmployerReleaseParm?): NetworkResponse<InviteTalentEmployerReleaseReq, HttpError>

}