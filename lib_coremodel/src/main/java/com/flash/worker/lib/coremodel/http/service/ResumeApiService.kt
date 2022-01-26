package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.SaveResumeParm
import com.flash.worker.lib.coremodel.data.parm.UpdateJobResumeParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ResumeDetailReq
import com.flash.worker.lib.coremodel.data.req.UserResumeReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.ResumeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface ResumeApiService {

    @POST(ResumeApi.SAVE_RESUME)
    suspend fun saveResume(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveResumeParm): NetworkResponse<BaseReq, HttpError>

    @GET(ResumeApi.USER_RESUMES)
    suspend fun getUserResumes(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<UserResumeReq, HttpError>

    @GET(ResumeApi.RESUME_DETAIL)
    suspend fun getResumeDetail(
            @Header("X-TOKEN") token: String?,
            @Query("resumeId") resumeId  : String?)
            : NetworkResponse<ResumeDetailReq, HttpError>

    @POST(ResumeApi.UPDATE_BASE_RESUME)
    suspend fun updateBaseResume(
            @Header("X-TOKEN") token: String?,
            @Body body: ResumeBaseInfo?): NetworkResponse<BaseReq, HttpError>

    @POST(ResumeApi.UPDATE_JOB_RESUME)
    suspend fun updateJobResume(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateJobResumeParm?): NetworkResponse<BaseReq, HttpError>

    @GET(ResumeApi.DELETE_RESUME)
    suspend fun deleteResume(
            @Header("X-TOKEN") token: String?,
            @Query("resumeId") resumeId  : String?): NetworkResponse<BaseReq, HttpError>
}