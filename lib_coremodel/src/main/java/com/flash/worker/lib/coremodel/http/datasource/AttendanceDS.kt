package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.AttendanceDateReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerAttendanceReq
import com.flash.worker.lib.coremodel.data.req.TalentAttendanceReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IAttendanceDS
import com.flash.worker.lib.coremodel.http.service.AttendanceApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class AttendanceDS:AbsDS(),IAttendanceDS {

    override val talentAttendanceData = MutableLiveData<HttpResult<TalentAttendanceReq>>()
    override suspend fun fetchTalentAttendance(token: String?,body: TalentAttendanceParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentAttendanceData.value = talentAttendanceReq(token,body)
        }
    }

    override val talentOnDutyData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun talentOnDuty(token: String?,body: TalentOndutyParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentOnDutyData.value = talentOnDutyReq(token,body)
        }
    }

    override val talentOffDutyData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun talentOffDuty(token: String?,body: TalentOffdutyParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentOffDutyData.value = talentOffDutyReq(token,body)
        }
    }

    override val employerAttendanceData = MutableLiveData<HttpResult<EmployerAttendanceReq>>()
    override suspend fun fetchEmployerAttendance(token: String?,body: EmployerAttendanceParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerAttendanceData.value = employerAttendanceReq(token,body)
        }
    }

    override val attendanceDateData = MutableLiveData<HttpResult<AttendanceDateReq>>()
    override suspend fun fetchAttendanceDate(token: String?,employerReleaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            attendanceDateData.value = attendanceDateReq(token,employerReleaseId)
        }
    }

    override val employerConfirmAttendanceData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerConfirmAttendance(token: String?,body: EmployerConfirmAttendanceParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerConfirmAttendanceData.value = employerConfirmAttendanceReq(token,body)
        }
    }

    private suspend fun <T> talentAttendanceReq(token: String?,body: TalentAttendanceParm?): T =
        withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .fetchTalentAttendance(token,body)) as T
    }

    private suspend fun <T> talentOnDutyReq(token: String?,body: TalentOndutyParm?): T =
        withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .talentOnDuty(token,body)) as T
    }

    private suspend fun <T> talentOffDutyReq(token: String?,body: TalentOffdutyParm?): T =
        withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .talentOffDuty(token,body)) as T
    }

    private suspend fun <T> employerAttendanceReq(token: String?,body: EmployerAttendanceParm?): T =
        withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .fetchEmployerAttendance(token,body)) as T
    }

    private suspend fun <T> attendanceDateReq(token: String?,employerReleaseId: String?): T =
        withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .fetchAttendanceDate(token,employerReleaseId)) as T
    }

    private suspend fun <T> employerConfirmAttendanceReq(
            token: String?,
            body: EmployerConfirmAttendanceParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AttendanceApiService::class.java)
                        .employerConfirmAttendance(token,body)) as T
    }


}