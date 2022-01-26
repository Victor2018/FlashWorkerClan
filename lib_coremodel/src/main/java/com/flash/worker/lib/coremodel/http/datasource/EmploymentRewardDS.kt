package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IEmploymentRewardDS
import com.flash.worker.lib.coremodel.http.service.EmploymentRewardApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentRewardDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentRewardDS: AbsDS(), IEmploymentRewardDS {

    override val employmentRewardData = MutableLiveData<HttpResult<EmploymentRewardReq>>()
    override suspend fun fetchEmploymentReward(token: String?,body: EmploymentRewardParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employmentRewardData.value = employmentRewardReq(token,body)
        }
    }

    override val receiveEmploymentRewardData = MutableLiveData<HttpResult<ReceiveEmploymentRewardReq>>()
    override suspend fun receiveEmploymentReward(token: String?,body: ReceiveEmploymentRewardParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveEmploymentRewardData.value = receiveEmploymentRewardReq(token,body)
        }
    }


    private suspend fun <T> employmentRewardReq(
            token: String?,body: EmploymentRewardParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentRewardApiService::class.java)
                .fetchEmployerReward(token,body)) as T
    }

    private suspend fun <T> receiveEmploymentRewardReq(
            token: String?,body: ReceiveEmploymentRewardParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentRewardApiService::class.java)
                .receiveEmploymentReward(token,body)) as T
    }


}