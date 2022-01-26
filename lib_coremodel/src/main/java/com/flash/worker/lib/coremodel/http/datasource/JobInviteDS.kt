package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerJobInviteParm
import com.flash.worker.lib.coremodel.data.parm.EmployerSendInviteParm
import com.flash.worker.lib.coremodel.data.parm.TalentJobInviteParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IJobInviteDS
import com.flash.worker.lib.coremodel.http.service.AccountApiService
import com.flash.worker.lib.coremodel.http.service.JobInviteApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobInviteDS
 * Author: Victor
 * Date: 2021/4/21 10:11
 * Description: 
 * -----------------------------------------------------------------
 */
class JobInviteDS: AbsDS(),IJobInviteDS {

    override val talentJobInviteData = MutableLiveData<HttpResult<TalentJobInviteReq>>()
    override suspend fun fetchTalentJobInvite(token: String?,body: TalentJobInviteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentJobInviteData.value = talentJobInviteReq(token,body)
        }
    }

    override val employerSendInviteData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerSendInvite(token: String?,body: EmployerSendInviteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerSendInviteData.value = employerSendInviteReq(token,body)
        }
    }

    override val employerJobInviteData = MutableLiveData<HttpResult<EmployerJobInviteReq>>()
    override suspend fun fetchEmployerJobInvite(token: String?,body: EmployerJobInviteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerJobInviteData.value = employerJobInviteReq(token,body)
        }
    }

    override val inviteNumData = MutableLiveData<HttpResult<InviteNumReq>>()
    override suspend fun fetchInviteNum(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            inviteNumData.value = inviteNumReq(token)
        }
    }

    private suspend fun <T> talentJobInviteReq(token: String?,body: TalentJobInviteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(JobInviteApiService::class.java)
                .fetchTalentJobInvite(token,body)) as T
    }

    private suspend fun <T> employerSendInviteReq(token: String?,body: EmployerSendInviteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(JobInviteApiService::class.java)
                .employerSendInvite(token,body)) as T
    }

    private suspend fun <T> employerJobInviteReq(token: String?,body: EmployerJobInviteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(JobInviteApiService::class.java)
                .fetchEmployerJobInvite(token,body)) as T
    }

    private suspend fun <T> inviteNumReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(JobInviteApiService::class.java)
                .fetchInviteNum(token)) as T
    }

}