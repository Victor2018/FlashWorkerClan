package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReportEmployerViolationParm
import com.flash.worker.lib.coremodel.data.parm.ReportTalentViolationParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ViolationUserReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IViolationReportDS
import com.flash.worker.lib.coremodel.http.service.ViolationReportApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationReportDS
 * Author: Victor
 * Date: 2021/7/15 17:42
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationReportDS: AbsDS(),IViolationReportDS {

    override val reportTalentViolationData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun reportTalentViolation(token: String?,body: ReportTalentViolationParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            reportTalentViolationData.value = reportTalentViolationReq(token,body)
        }
    }

    override val reportEmployerViolationData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun reportEmployerViolation(token: String?,body: ReportEmployerViolationParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            reportEmployerViolationData.value = reportEmployerViolationReq(token,body)
        }
    }

    override val violationUserData = MutableLiveData<HttpResult<ViolationUserReq>>()
    override suspend fun fetchViolationUser(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            violationUserData.value = fetchViolationUserReq(token)
        }
    }

    private suspend fun <T> reportTalentViolationReq(token: String?,body: ReportTalentViolationParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(ViolationReportApiService::class.java)
                .reportTalentViolation(token,body)) as T
    }

    private suspend fun <T> reportEmployerViolationReq(token: String?,body: ReportEmployerViolationParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(ViolationReportApiService::class.java)
                .reportEmployerViolation(token,body)) as T
    }

    private suspend fun <T> fetchViolationUserReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(ViolationReportApiService::class.java)
                .fetchViolationUser(token)) as T
    }

}