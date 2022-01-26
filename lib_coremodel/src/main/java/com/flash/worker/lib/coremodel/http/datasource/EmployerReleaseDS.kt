package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseReq
import com.flash.worker.lib.coremodel.data.req.InviteTalentEmployerReleaseReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerReleaseDS
import com.flash.worker.lib.coremodel.http.service.EmployerReleaseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseDS
 * Author: Victor
 * Date: 2021/4/13 11:00
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleaseDS: AbsDS(), IEmployerReleaseDS {

    override val saveEmployerDraftsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun saveEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveEmployerDraftsData.value = saveEmployerDraftsReq(token,body)
        }
    }

    override val saveEmployerReleaseData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun saveEmployerRelease(token: String?,body: SaveEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveEmployerReleaseData.value = saveEmployerReleaseReq(token,body)
        }
    }

    override val employerReleaseData = MutableLiveData<HttpResult<EmployerReleaseReq>>()
    override suspend fun fetchEmployerRelease(token: String?,body: EmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseData.value = fetchEmployerReleaseReq(token,body)
        }
    }

    override val employerReleaseRefreshData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerReleaseRefresh(token: String?,body: EmployerReleaseRefreshParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseRefreshData.value = employerReleaseReFreshReq(token,body)
        }
    }

    override val employerReleaseOffShelfData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerReleaseOffShelf(token: String?,body: EmployerReleaseOffShelfParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseOffShelfData.value = employerReleaseOffShelfReq(token,body)
        }
    }

    override val employerReleaseNewReleaseData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerReleaseNewRelease(token: String?,body: EmployerReleaseNewReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseNewReleaseData.value = employerReleaseNewReleaseReq(token,body)
        }
    }

    override val employerReleaseDeleteData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerReleaseDelete(token: String?,body: EmployerReleaseDeleteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseDeleteData.value = employerReleaseDeleteReq(token,body)
        }
    }

    override val employerReleaseDetailData = MutableLiveData<HttpResult<EmployerReleaseDetailReq>>()
    override suspend fun fetchEmployerReleaseDetail(token: String?,releaseId : String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleaseDetailData.value = employerReleaseDetailReq(token,releaseId)
        }
    }

    override val updateEmployerDraftsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateEmployerDraftsData.value = updateEmployerDraftsReq(token,body)
        }
    }

    override val updateEmployerReleaseData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateEmployerRelease(token: String?,body: SaveEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateEmployerReleaseData.value = updateEmployerReleaseReq(token,body)
        }
    }

    override val inviteTalentEmployerReleaseData = MutableLiveData<HttpResult<InviteTalentEmployerReleaseReq>>()
    override suspend fun fetchInviteTalentEmployerRelease(token: String?,body: InviteTalentEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            inviteTalentEmployerReleaseData.value = inviteTalentEmployerReleaseReq(token,body)
        }
    }

    private suspend fun <T> saveEmployerDraftsReq(
            token: String?,
            body: SaveEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .saveEmployerDrafts(token,body)) as T
    }

    private suspend fun <T> saveEmployerReleaseReq(
            token: String?,
            body: SaveEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .saveEmployerRelease(token,body)) as T
    }

    private suspend fun <T> fetchEmployerReleaseReq(
            token: String?,
            body: EmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .fetchEmployerRelease(token,body)) as T
    }

    private suspend fun <T> employerReleaseReFreshReq(
            token: String?,
            body: EmployerReleaseRefreshParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .employerReleaseRefresh(token,body)) as T
    }

    private suspend fun <T> employerReleaseOffShelfReq(
            token: String?,
            body: EmployerReleaseOffShelfParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .employerReleaseOffShelf(token,body)) as T
    }

    private suspend fun <T> employerReleaseNewReleaseReq(
            token: String?,
            body: EmployerReleaseNewReleaseParm?):T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .employerReleaseNewRelease(token,body)) as T
    }

    private suspend fun <T> employerReleaseDeleteReq(
            token: String?,
            body: EmployerReleaseDeleteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .employerReleaseDelete(token,body)) as T
    }

    private suspend fun <T> employerReleaseDetailReq(
            token: String?,
            releaseId : String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .fetchEmployerReleaseDetail(token,releaseId)) as T
    }

    private suspend fun <T> updateEmployerDraftsReq(
            token: String?,
            body: SaveEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .updateEmployerDrafts(token,body)) as T
    }

    private suspend fun <T> updateEmployerReleaseReq(
            token: String?,
            body: SaveEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .updateEmployerRelease(token,body)) as T
    }

    private suspend fun <T> inviteTalentEmployerReleaseReq(
            token: String?,
            body: InviteTalentEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerReleaseApiService::class.java)
                .fetchInviteTalentEmployerRelease(token,body)) as T
    }

}