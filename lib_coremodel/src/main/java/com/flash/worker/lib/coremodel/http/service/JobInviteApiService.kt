package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.BalanceFlowParm
import com.flash.worker.lib.coremodel.data.parm.EmployerJobInviteParm
import com.flash.worker.lib.coremodel.data.parm.EmployerSendInviteParm
import com.flash.worker.lib.coremodel.data.parm.TalentJobInviteParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.JobInviteApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobInviteApiService
 * Author: Victor
 * Date: 2021/4/20 20:10
 * Description: 
 * -----------------------------------------------------------------
 */
interface JobInviteApiService {

    @POST(JobInviteApi.TALENT_JOB_INVITE)
    suspend fun fetchTalentJobInvite(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentJobInviteParm?): NetworkResponse<TalentJobInviteReq, HttpError>

    @POST(JobInviteApi.EMPLOYER_SEND_INVITE)
    suspend fun employerSendInvite(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerSendInviteParm?): NetworkResponse<BaseReq, HttpError>

    @POST(JobInviteApi.EMPLOYER_JOB_INVITE)
    suspend fun fetchEmployerJobInvite(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerJobInviteParm?): NetworkResponse<EmployerJobInviteReq, HttpError>

    @GET(JobInviteApi.INVITE_NUM)
    suspend fun fetchInviteNum(
            @Header("X-TOKEN") token: String?): NetworkResponse<InviteNumReq, HttpError>

}