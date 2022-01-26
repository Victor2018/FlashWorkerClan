package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.DisputeApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface DisputeApiService {

    @GET(DisputeApi.REPORT_CONFIRM_DETAIL)
    suspend fun fetchReportConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Query("jobOrderId") jobOrderId: String?): NetworkResponse<ReportConfirmDetailReq, HttpError>

    @GET(DisputeApi.COMPLAINT_CONFIRM_DETAIL)
    suspend fun fetchComplaintConfirmDetail(
            @Header("X-TOKEN") token: String?,
            @Query("jobOrderId") jobOrderId: String?): NetworkResponse<ComplaintConfirmDetailReq, HttpError>

    @POST(DisputeApi.REPORT)
    suspend fun report(
            @Header("X-TOKEN") token: String?,
            @Body body: ReportParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.UPDATE_REPORT)
    suspend fun updateReport(
            @Header("X-TOKEN") token: String?,
            @Body body: ReportParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.COMPLAINT)
    suspend fun complaint(
            @Header("X-TOKEN") token: String?,
            @Body body: ComplaintParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.TALENT_DISPUTE)
    suspend fun fetchTalentDispute(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentDisputeParm?): NetworkResponse<TalentDisputeReq, HttpError>

    @POST(DisputeApi.EMPLOYER_DISPUTE)
    suspend fun fetchEmployerDispute(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerDisputeParm?): NetworkResponse<EmployerDisputeReq, HttpError>

    @GET(DisputeApi.DISPUTE_DETAIL)
    suspend fun fetchDisputeDetail(
            @Header("X-TOKEN") token: String?,
            @Query("complaintNo") complaintNo : String?): NetworkResponse<DisputeDetailReq, HttpError>

    @POST(DisputeApi.APPLY_PLATFORM_ACCESS)
    suspend fun applyPlatformAccess(
            @Header("X-TOKEN") token: String?,
            @Body body : ApplyPlatformAccessParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.CANCEL_COMPLAINT)
    suspend fun cancelComplaint(
            @Header("X-TOKEN") token: String?,
            @Body body : CancelComplaintParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.TALENT_DELETE_DISPUTE)
    suspend fun talentDeleteDispute(
            @Header("X-TOKEN") token: String?,
            @Body body : TalentDeleteDisputeParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.EMPLOYER_DELETE_DISPUTE)
    suspend fun employerDeleteDispute(
            @Header("X-TOKEN") token: String?,
            @Body body : EmployerDeleteDisputeParm?): NetworkResponse<BaseReq, HttpError>

    @POST(DisputeApi.AGREE_COMPLAINT)
    suspend fun agreeComplaint(
            @Header("X-TOKEN") token: String?,
            @Body body : AgreeComplaintParm?): NetworkResponse<BaseReq, HttpError>

    @GET(DisputeApi.DISPUTE_HISTORY)
    suspend fun fetchDisputeHistory(
        @Header("X-TOKEN") token: String?,
        @Query("complaintNo") complaintNo : String?): NetworkResponse<DisputeHistoryReq, HttpError>

    @POST(DisputeApi.UPDATE_COMPLAINT)
    suspend fun updateComplaint(
        @Header("X-TOKEN") token: String?,
        @Body body: ComplaintParm?): NetworkResponse<BaseReq, HttpError>

}