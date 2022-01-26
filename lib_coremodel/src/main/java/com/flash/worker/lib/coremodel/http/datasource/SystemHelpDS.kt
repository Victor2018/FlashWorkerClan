package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ISystemHelpDS
import com.flash.worker.lib.coremodel.http.service.SystemHelpApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemHelpDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemHelpDS: AbsDS(), ISystemHelpDS {

    override val customerServiceData = MutableLiveData<HttpResult<CustomerServiceReq>>()
    override suspend fun fetchCustomerService(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            customerServiceData.value = customerServiceReq(token)
        }
    }


    private suspend fun <T> customerServiceReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(SystemHelpApiService::class.java)
                        .fetchCustomerService(token)) as T
    }



}