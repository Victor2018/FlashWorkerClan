package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.AttendanceDateReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerAttendanceReq
import com.flash.worker.lib.coremodel.data.req.TalentAttendanceReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.AttendanceApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface AttendanceApiService {
    @POST(AttendanceApi.TALENT_ATTENDANCE)
    suspend fun fetchTalentAttendance(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentAttendanceParm?): NetworkResponse<TalentAttendanceReq, HttpError>

    @POST(AttendanceApi.TALENT_ONDUTY)
    suspend fun talentOnDuty(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentOndutyParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AttendanceApi.TALENT_OFFDUTY)
    suspend fun talentOffDuty(
            @Header("X-TOKEN") token: String?,
            @Body body: TalentOffdutyParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AttendanceApi.EMPLOYER_ATTENDANCE)
    suspend fun fetchEmployerAttendance(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerAttendanceParm?): NetworkResponse<EmployerAttendanceReq, HttpError>

    @GET(AttendanceApi.ATTENDANCE_DATE)
    suspend fun fetchAttendanceDate(
            @Header("X-TOKEN") token: String?,
            @Query("employerReleaseId") employerReleaseId: String?): NetworkResponse<AttendanceDateReq, HttpError>

    @POST(AttendanceApi.EMPLOYER_CONFIRM_ATTENDANCE)
    suspend fun employerConfirmAttendance(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerConfirmAttendanceParm?): NetworkResponse<BaseReq, HttpError>

}