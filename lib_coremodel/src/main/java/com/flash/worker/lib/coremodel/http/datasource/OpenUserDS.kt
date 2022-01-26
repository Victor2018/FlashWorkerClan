package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.BindAliPayParm
import com.flash.worker.lib.coremodel.data.parm.OpenUserUnBindParm
import com.flash.worker.lib.coremodel.data.req.AliAuthReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.OpenUserInfoReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IOpenUserDS
import com.flash.worker.lib.coremodel.http.service.OpenUserApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OpenUserDS
 * Author: Victor
 * Date: 2021/4/13 14:24
 * Description: 
 * -----------------------------------------------------------------
 */
class OpenUserDS: AbsDS(), IOpenUserDS {

    override val bindAliPayData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun bindAliPay(token: String?,body: BindAliPayParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            bindAliPayData.value = bindAliPayReq(token,body)
        }
    }

    override val aliAuthData = MutableLiveData<HttpResult<AliAuthReq>>()
    override suspend fun fetchAliAuthInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            aliAuthData.value = fetchAliAuthInfoReq(token)
        }
    }

    override val openUserInfoData = MutableLiveData<HttpResult<OpenUserInfoReq>>()
    override suspend fun fetchOpenUserInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            openUserInfoData.value = fetchOpenUserInfoReq(token)
        }
    }

    override val openUserUnBindData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun openUserUnBind(token: String?,body: OpenUserUnBindParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            openUserUnBindData.value = openUserUnBindReq(token,body)
        }
    }

    private suspend fun <T> bindAliPayReq(token: String?, body: BindAliPayParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(OpenUserApiService::class.java)
                .bindAliPay(token,body)) as T

    }

    private suspend fun <T> fetchAliAuthInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(OpenUserApiService::class.java)
                .fetchAliAuthInfo(token)) as T
    }


    private suspend fun <T> fetchOpenUserInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(OpenUserApiService::class.java)
                .fetchOpenUserInfo(token)) as T
    }

    private suspend fun <T> openUserUnBindReq(token: String?,body: OpenUserUnBindParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(OpenUserApiService::class.java)
                .openUserUnBind(token,body)) as T
    }

}