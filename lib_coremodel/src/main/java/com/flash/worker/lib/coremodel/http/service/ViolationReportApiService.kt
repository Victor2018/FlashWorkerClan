package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.ApplyEstablishGuildParm
import com.flash.worker.lib.coremodel.data.parm.ReportEmployerViolationParm
import com.flash.worker.lib.coremodel.data.parm.ReportTalentViolationParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ViolationUserReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.GuildApi
import com.flash.worker.lib.coremodel.http.api.ViolationReportApi
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationReportApiService
 * Author: Victor
 * Date: 2021/7/15 17:29
 * Description: 
 * -----------------------------------------------------------------
 */
interface ViolationReportApiService {

    @POST(ViolationReportApi.REPORT_TALENT_VIOLATION)
    suspend fun reportTalentViolation(
            @Header("X-TOKEN") token: String?,
            @Body body: ReportTalentViolationParm?): NetworkResponse<BaseReq, HttpError>

    @POST(ViolationReportApi.REPORT_EMPLOYER_VIOLATION)
    suspend fun reportEmployerViolation(
            @Header("X-TOKEN") token: String?,
            @Body body: ReportEmployerViolationParm?): NetworkResponse<BaseReq, HttpError>

    @POST(ViolationReportApi.VIOLATION_USER)
    suspend fun fetchViolationUser(
            @Header("X-TOKEN") token: String?): NetworkResponse<ViolationUserReq, HttpError>

}