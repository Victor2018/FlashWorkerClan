package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.TalentJobApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface TalentJobApiService {

    @POST(TalentJobApi.TALENT_WAIT_EMPLOY)
    suspend fun fetchTalentWaitEmploy(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentWaitEmployParm?)
            : NetworkResponse<TalentWaitEmployReq, HttpError>

    @POST(TalentJobApi.TALENT_EMPLOYING)
    suspend fun fetchTalentEmploying(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentEmployingParm?): NetworkResponse<TalentEmployingReq, HttpError>

    @POST(TalentJobApi.TALENT_WAIT_COMMENT)
    suspend fun fetchTalentWaitComment(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentWaitCommentParm?): NetworkResponse<TalentWaitCommentReq, HttpError>

    @POST(TalentJobApi.TALENT_JOB_FINISH)
    suspend fun fetchTalentJobFinish(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentJobFinishParm?): NetworkResponse<TalentJobFinishReq, HttpError>

    @POST(TalentJobApi.TALENT_ORDER_DELETE)
    suspend fun talentJobDelete(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentJobDeleteParm?): NetworkResponse<BaseReq, HttpError>

    @GET(TalentJobApi.TALENT_ORDER_DETAIL)
    suspend fun fetchTalentOrderDetail(
            @Header("X-TOKEN") token: String?,
            @Query("jobOrderId") jobOrderId : String?)
            : NetworkResponse<TalentOrderDetailReq, HttpError>

    @GET(TalentJobApi.SETTLEMENT_DATE)
    suspend fun fetchSettlementDate(
            @Header("X-TOKEN") token: String?,
            @Query("employerReleaseId") employerReleaseId : String?)
            : NetworkResponse<SettlementDateReq, HttpError>

    @POST(TalentJobApi.TALENT_SETTLEMENT_ORDER)
    suspend fun fetchTalentSettlementOrder(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentSettlementOrderParm?)
            : NetworkResponse<TalentSettlementOrderReq, HttpError>

    @POST(TalentJobApi.CANCEL_JOB_CONFIRM_DETAIL)
    suspend fun fetchCancelJobConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Body body: CancelJobConfirmDetailParm?)
            : NetworkResponse<CancelJobConfirmDetailReq, HttpError>

    @POST(TalentJobApi.CANCEL_JOB)
    suspend fun cancelJob(
            @Header("X-TOKEN") token: String?,
            @Body body: CancelJobParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentJobApi.ARRIVE_POST)
    suspend fun arrivePost(
            @Header("X-TOKEN") token: String?,
            @Body body: ArrivePostParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentJobApi.FINISH_JOB)
    suspend fun finishJob(
            @Header("X-TOKEN") token: String?,
            @Body body: FinishJobParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentJobApi.REMIND_PREPAID)
    suspend fun remindPrepaid(
            @Header("X-TOKEN") token: String?,
            @Body body: RemindPrepaidParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentJobApi.REMIND_SETTLEMENT)
    suspend fun remindSettlement(
            @Header("X-TOKEN") token: String?,
            @Body body: RemindSettlementParm?): NetworkResponse<BaseReq, HttpError>

    @GET(TalentJobApi.UNREAD_STATUS)
    suspend fun fetchUnReadStatus(
        @Header("X-TOKEN") token: String?)
            : NetworkResponse<TalentUnReadStatusReq, HttpError>

    @POST(TalentJobApi.TASK_SETTLEMENT_DETAIL)
    suspend fun fetchTaskSettlementDetail(
        @Header("X-TOKEN") token: String?,
        @Body body: TaskSettlementDetailParm?): NetworkResponse<TaskSettlementDetailReq, HttpError>

    @POST(TalentJobApi.TASK_SUBMIT)
    suspend fun taskSubmit(
        @Header("X-TOKEN") token: String?,
        @Body body: TaskSubmitParm?): NetworkResponse<BaseReq, HttpError>

    @POST(TalentJobApi.TASK_SUBMIT_DETAIL)
    suspend fun fetchTaskSubmitDetail(
        @Header("X-TOKEN") token: String?,
        @Body body: TaskSubmitDetailParm?): NetworkResponse<TaskSubmitDetailReq, HttpError>

}