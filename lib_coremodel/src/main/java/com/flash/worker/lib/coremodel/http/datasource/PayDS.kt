package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.PayParm
import com.flash.worker.lib.coremodel.data.parm.PayQueryParm
import com.flash.worker.lib.coremodel.data.req.PayQueryReq
import com.flash.worker.lib.coremodel.data.req.PayReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IPayDS
import com.flash.worker.lib.coremodel.http.service.PayApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class PayDS: AbsDS(), IPayDS {

    override val payData = MutableLiveData<HttpResult<PayReq>>()
    override suspend fun pay(token: String?,body: PayParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            payData.value = payReq(token,body)
        }
    }

    override val payQueryData = MutableLiveData<HttpResult<PayQueryReq>>()
    override suspend fun payQuery(token: String?,body: PayQueryParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            payQueryData.value = payQueryReq(token,body)
        }
    }

    private suspend fun <T> payReq(token: String?,body: PayParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(PayApiService::class.java)
                        .pay(token,body)) as T
    }

    private suspend fun <T> payQueryReq(token: String?,body: PayQueryParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(PayApiService::class.java)
                        .payQuery(token,body)) as T
    }


}