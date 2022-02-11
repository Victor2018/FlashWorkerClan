package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmployerJobApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmployerJobApiService {
    
    @POST(EmployerJobApi.EMPLOYER_WAIT_EMPLOY)
    suspend fun fetchEmployerWaitEmploy(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerWaitEmployParm?)
            : NetworkResponse<EmployerWaitEmployReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_JOB_DELETE)
    suspend fun employerJobDelete(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerJobDeleteParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_SIGN_UP_USER)
    suspend fun fetchEmployerSignUpUser(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerSignUpUserParm?)
            : NetworkResponse<EmployerSignUpUserReq, HttpError>

    @POST(EmployerJobApi.TALENT_USER)
    suspend fun fetchTalentUser(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentUserParm?): NetworkResponse<TalentUserReq, HttpError>

    @GET(EmployerJobApi.EMPLOYMENT_NUM)
    suspend fun fetchEmploymentNum(
            @Header("X-TOKEN") token: String?,
            @Query("employerReleaseId") employerReleaseId : String?)
            : NetworkResponse<EmploymentNumReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_EMPLOYING)
    suspend fun fetchEmployerEmploying(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerEmployingParm?)
            : NetworkResponse<EmployerEmployingReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_SETTLEMENT_ORDER)
    suspend fun fetchEmployerSettlementOrder(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerSettlementOrderParm?)
            : NetworkResponse<EmployerSettlementOrderReq, HttpError>

    @POST(EmployerJobApi.SETTLEMENT_NUM)
    suspend fun fetchSettlementNum(
            @Header("X-TOKEN") token: String?,
            @Body body: SettlementNumParm?): NetworkResponse<SettlementNumReq, HttpError>

    @POST(EmployerJobApi.FIRE_TALENT_CONFIRM_DETAIL)
    suspend fun fireTalentConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: FireTalentConfirmDetailParm?)
            : NetworkResponse<FireTalentConfirmDetailReq, HttpError>

    @POST(EmployerJobApi.FIRE_TALENT)
    suspend fun fireTalent(
            @Header("X-TOKEN") token: String?,
            @Body body: FireTalentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_JOB_CANCELED)
    suspend fun fetchEmployerCancelled(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerCancelledParm?)
            : NetworkResponse<EmployerCancelledReq, HttpError>


    @POST(EmployerJobApi.EMPLOYER_WAIT_COMMENT)
    suspend fun fetchEmployerWaitComment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerWaitCommentParm?)
            : NetworkResponse<EmployerWaitCommentReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_JOB_FINISH)
    suspend fun fetchEmployerJobFinish(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerJobFinishParm?): NetworkResponse<EmployerJobFinishReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_ORDER_DELETE)
    suspend fun employerOrderDelete(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerOrderDeleteParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_FINISH_USER)
    suspend fun fetchEmployerFinishUser(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerFinishUserParm?): NetworkResponse<EmployerFinishUserReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_WAIT_COMMETN_USER)
    suspend fun fetchEmployerWaitCommentUser(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerWaitCommentUserParm?): NetworkResponse<EmployerWaitCommentUserReq, HttpError>

    @POST(EmployerJobApi.EMPLOYER_RELEASING)
    suspend fun fetchEmployerReleasing(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerReleasingParm?): NetworkResponse<EmployerReleasingReq, HttpError>

    @GET(EmployerJobApi.UNREAD_STATUS)
    suspend fun fetchUnReadStatus(
        @Header("X-TOKEN") token: String?)
            : NetworkResponse<EmployerUnReadStatusReq, HttpError>

    @POST(EmployerJobApi.TASK_SETTLEMENT)
    suspend fun fetchTaskSettlement(
        @Header("X-TOKEN") token: String?,
        @Body body: TaskSettledParm?): NetworkResponse<TaskSettledReq, HttpError>

    @POST(EmployerJobApi.CHECK_AUTO_PREPAID)
    suspend fun checkAutoPrepaid(
        @Header("X-TOKEN") token: String?,
        @Body body: CheckAutoPrepaidParm?): NetworkResponse<CheckAutoPrepaidReq, HttpError>

    @POST(EmployerJobApi.OPEN_AUTO_PREPAID)
    suspend fun openAutoPrepaid(
        @Header("X-TOKEN") token: String?,
        @Body body: OpenAutoPrepaidParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerJobApi.CLOSE_AUTO_PREPAID)
    suspend fun closeAutoPrepaid(
        @Header("X-TOKEN") token: String?,
        @Body body: CloseAutoPrepaidParm?): NetworkResponse<BaseReq, HttpError>
}