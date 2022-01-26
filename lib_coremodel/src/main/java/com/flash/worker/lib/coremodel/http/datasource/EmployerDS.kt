package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerParm
import com.flash.worker.lib.coremodel.data.parm.UpdateEmployerParm
import com.flash.worker.lib.coremodel.data.req.EditEmployerDetailReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployersReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerDS
import com.flash.worker.lib.coremodel.http.service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDS
 * Author: Victor
 * Date: 2020/12/19 10:00
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerDS: AbsDS(), IEmployerDS {

    override val saveEmployerData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun saveEmployer(token: String?,body: SaveEmployerParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveEmployerData.value = saveEmployerReq(token,body)
        }
    }

    override val employersData = MutableLiveData<HttpResult<EmployersReq>>()
    override suspend fun getEmployers(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employersData.value = employersReq(token)
        }
    }

    override val editEmployerDetailData = MutableLiveData<HttpResult<EditEmployerDetailReq>>()
    override suspend fun getEditEmployerDetail(token: String?,employerId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            editEmployerDetailData.value = editEmployerDetailReq(token,employerId)
        }
    }

    override val updateEmployerData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateEmployer(token: String?,body: UpdateEmployerParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateEmployerData.value = updateEmployerReq(token,body)
        }
    }

    override val deleteEmployerData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun deleteEmployer(token: String?,employerId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            deleteEmployerData.value = deleteEmployerReq(token,employerId)
        }
    }

    override val inviteTalentEmployerData = MutableLiveData<HttpResult<EmployersReq>>()
    override suspend fun fetchInviteTalentEmployer(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            inviteTalentEmployerData.value = inviteTalentEmployerReq(token)
        }
    }

    private suspend fun <T> saveEmployerReq(
            token: String?,
            body: SaveEmployerParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                        .saveEmployer(token,body)) as T
    }

    private suspend fun <T> employersReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                        .getEmployers(token)) as T
    }

    private suspend fun <T> editEmployerDetailReq(
            token: String?,
            employerId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                        .getEditEmployerDetail(token,employerId)) as T
    }

    private suspend fun <T> updateEmployerReq(
            token: String?,
            body: UpdateEmployerParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                .updateEmployer(token,body)) as T
    }

    private suspend fun <T> deleteEmployerReq(
            token: String?,
            employerId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                .deleteEmployer(token,employerId)) as T
    }

    private suspend fun <T> inviteTalentEmployerReq(
            token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerApiService::class.java)
                .fetchInviteTalentEmployer(token)) as T
    }

}