package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmploymentApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmploymentApiService {

    @POST(EmploymentApi.CHECK_SIGN_UP)
    suspend fun checkSingUp(
            @Header("X-TOKEN") token: String?,
            @Body body: CheckSignUpParm?): NetworkResponse<CheckSignUpReq, HttpError>

    @POST(EmploymentApi.SIGN_UP)
    suspend fun singUp(
            @Header("X-TOKEN") token: String?,
            @Body body: SignUpParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.CANCEL_SIGN_UP)
    suspend fun talentCancelSignUp(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentCancelSignUpParm?): NetworkResponse<BaseReq, HttpError>

    @GET(EmploymentApi.SIGN_UP_CONFIRM_DETAIL)
    suspend fun fetchSignUpConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Query("employerReleaseId") releaseId : String?
    ): NetworkResponse<SignUpConfirmDetailReq, HttpError>

    @GET(EmploymentApi.EMPLOYER_DETAIL)
    suspend fun fetchEmployerDetail(
            @Header("X-TOKEN") token: String?,
            @Query("employerId") employerId: String?)
            : NetworkResponse<EmployerDetailReq, HttpError>

    @GET(EmploymentApi.TALENT_RESUME_DETAIL)
    suspend fun fetchTalentResumeDetail(
            @Header("X-TOKEN") token: String?,
            @Query("resumeId") resumeId: String?)
            : NetworkResponse<TalentResumeDetialReq, HttpError>

    @POST(EmploymentApi.EMPLOYER_REFUSE)
    suspend fun employerRefuse(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerRefuseParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.EMPLOY_CONFIRM_DETAIL)
    suspend fun fetchEmployConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployConfirmDetailParm?
    ): NetworkResponse<EmployConfirmDetailReq, HttpError>

    @POST(EmploymentApi.EMPLOYER_EMPLOYMENT)
    suspend fun employerEmployment(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerEmploymentParm?
    ): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.PREPAID_CONFIRM_DETAIL)
    suspend fun fetchPrepaidConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: PrepaidConfirmDetailParm?): NetworkResponse<PrepaidConfirmDetailReq, HttpError>

    @POST(EmploymentApi.PREPAID)
    suspend fun prepaid(
            @Header("X-TOKEN") token: String?,
            @Body body: PrepaidParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.REWARD_CONFIRM_DETAIL)
    suspend fun fetchRewardConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: RewardConfirmDetailParm?)
            : NetworkResponse<RewardConfirmDetailReq, HttpError>

    @POST(EmploymentApi.REWARD)
    suspend fun reward(
            @Header("X-TOKEN") token: String?,
            @Body body: RewardParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.SETTLEMENT_CONFIRM_DETAIL)
    suspend fun settlementConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: SettlementConfirmDetailParm?)
            : NetworkResponse<SettlementConfirmDetailReq, HttpError>

    @POST(EmploymentApi.SETTLEMENT)
    suspend fun settlement(
            @Header("X-TOKEN") token: String?,
            @Body body: SettlementParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.CHECK_TERMINATION_EMPLOYMENT)
    suspend fun checkTerminationEmployment(
            @Header("X-TOKEN") token: String?,
            @Body body: CheckTerminationEmploymentParm?): NetworkResponse<CheckTerminationEmploymentReq, HttpError>

    @POST(EmploymentApi.TERMINATION_EMPLOYMENT)
    suspend fun terminationEmployment(
            @Header("X-TOKEN") token: String?,
            @Body body: TerminationEmploymentParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.RECEIVE_TASK_DETAIL)
    suspend fun fetchReceiveTaskDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: ReceiveTaskDetailParm?): NetworkResponse<ReceiveTaskDetailReq, HttpError>

    @POST(EmploymentApi.RECEIVE_TASK)
    suspend fun receiveTask(
            @Header("X-TOKEN") token: String?,
            @Body body: ReceiveTaskParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.TASK_SETTLEMENT_CONFIRM_DETAIL)
    suspend fun fetchTaskSettlementConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: TaskSettlementConfirmDetailParm?): NetworkResponse<TaskSettlementConfirmDetailReq, HttpError>

    @POST(EmploymentApi.TASK_SETTLEMENT)
    suspend fun taskSettlement(
            @Header("X-TOKEN") token: String?,
            @Body body: TaskSettlementParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.CHECK_CLOSE_TASK)
    suspend fun checkCloseTask(
            @Header("X-TOKEN") token: String?,
            @Body body: CheckCloseTaskParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmploymentApi.CLOSE_TASK)
    suspend fun closeTask(
            @Header("X-TOKEN") token: String?,
            @Body body: CloseTaskParm?): NetworkResponse<BaseReq, HttpError>

}