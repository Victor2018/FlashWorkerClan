package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ShareGuidInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareTaskInfoParm
import com.flash.worker.lib.coremodel.data.req.ShareInfoReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IShareDS
import com.flash.worker.lib.coremodel.http.service.AccountApiService
import com.flash.worker.lib.coremodel.http.service.ShareApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareDS
 * Author: Victor
 * Date: 2021/8/19 17:15
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareDS: AbsDS(), IShareDS {

    override val shareInfoData = MutableLiveData<HttpResult<ShareInfoReq>>()
    override suspend fun fetchShareInfo(token: String?,body: ShareInfoParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            shareInfoData.value = fetchShareInfoReq(token,body)
        }
    }

    override val shareGuildInfoData = MutableLiveData<HttpResult<ShareInfoReq>>()
    override suspend fun fetchShareGuildInfo(token: String?,body: ShareGuidInfoParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            shareGuildInfoData.value = fetchShareGuildInfoReq(token,body)
        }
    }

    override val shareTaskInfoData = MutableLiveData<HttpResult<ShareInfoReq>>()
    override suspend fun fetchShareTaskInfo(token: String?,body: ShareTaskInfoParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            shareTaskInfoData.value = fetchShareTaskInfoReq(token,body)
        }
    }

    private suspend fun <T> fetchShareInfoReq(token: String?,body: ShareInfoParm?): T = withContext(Dispatchers.IO) {
        handleRespone(
            ApiClient.getApiService(ShareApiService::class.java)
            .fetchShareInfo(token,body)) as T
    }

    private suspend fun <T> fetchShareGuildInfoReq(token: String?,body: ShareGuidInfoParm?): T = withContext(Dispatchers.IO) {
        handleRespone(
            ApiClient.getApiService(ShareApiService::class.java)
            .fetchShareGuildInfo(token,body)) as T
    }

    private suspend fun <T> fetchShareTaskInfoReq(token: String?,body: ShareTaskInfoParm?): T = withContext(Dispatchers.IO) {
        handleRespone(
            ApiClient.getApiService(ShareApiService::class.java)
            .fetchShareTaskInfo(token,body)) as T
    }
}