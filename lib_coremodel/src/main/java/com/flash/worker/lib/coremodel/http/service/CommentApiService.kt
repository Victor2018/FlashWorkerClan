package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.CommentApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommentApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface CommentApiService {

    @POST(CommentApi.COMMENT_TALENT)
    suspend fun commentTalent(
            @Header("X-TOKEN") token: String?,
            @Body body: CommentTalentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(CommentApi.COMMENT_ALL_TALENT)
    suspend fun commentAllTalent(
            @Header("X-TOKEN") token: String?,
            @Body body: CommentAllTalentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(CommentApi.COMMENT_EMPLOYER)
    suspend fun commentEmployer(
            @Header("X-TOKEN") token: String?,
            @Body body: CommentEmployerParm?): NetworkResponse<BaseReq, HttpError>

    @POST(CommentApi.TALENT_COMMENT_CENTER)
    suspend fun fetchTalentCommentCenter(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentCommentCenterParm?): NetworkResponse<TalentCommentCenterReq, HttpError>

    @POST(CommentApi.EMPLOYER_COMMENT_CENTER)
    suspend fun fetchEmployerCommentCenter(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerCommentCenterParm?): NetworkResponse<EmployerCommentCenterReq, HttpError>

    @POST(CommentApi.TALENT_DELETE_COMMENT)
    suspend fun talentDeleteComment(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentDeleteCommentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(CommentApi.EMPLOYER_DELETE_COMMENT)
    suspend fun employerDeleteComment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerDeleteCommentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(CommentApi.TALENT_LAST_COMMENT)
    suspend fun fetchTalentLastComment(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentLastCommentParm?): NetworkResponse<TalentLastCommentReq, HttpError>

    @POST(CommentApi.TALENT_COMMENT_STATISTICS)
    suspend fun fetchTalentCommentStatistics(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentCommentStatisticsParm?): NetworkResponse<TalentCommentStatisticsReq, HttpError>

    @POST(CommentApi.TALENT_COMMENT)
    suspend fun fetchTalentComment(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentCommentParm?): NetworkResponse<TalentCommentReq, HttpError>

    @POST(CommentApi.EMPLOYER_LAST_COMMENT)
    suspend fun fetchEmployerLastComment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerLastCommentParm?): NetworkResponse<EmployerLastCommentReq, HttpError>

    @POST(CommentApi.EMPLOYER_COMMENT_STATISTICS)
    suspend fun fetchEmployerCommentStatistics(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerCommentStatisticsParm?): NetworkResponse<EmployerCommentStatisticsReq, HttpError>

    @POST(CommentApi.EMPLOYER_COMMENT)
    suspend fun fetchEmployerComment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerCommentParm?): NetworkResponse<EmployerCommentReq, HttpError>

    @POST(CommentApi.EMPLOYER_USER_COMMENT)
    suspend fun fetchEmployerUserComment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerUserCommentParm?): NetworkResponse<EmployerUserCommentReq, HttpError>

}