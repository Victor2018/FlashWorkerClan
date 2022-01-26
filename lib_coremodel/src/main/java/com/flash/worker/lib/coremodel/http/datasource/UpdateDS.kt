package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IUpdateDS
import com.flash.worker.lib.coremodel.http.service.UpdateApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateDS: AbsDS(), IUpdateDS {

    override val latestVersionData = MutableLiveData<HttpResult<LatestVersionReq>>()
    override suspend fun fetchLatestVersion(body: LatestVersionParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            latestVersionData.value = latestVersionReq(body)
        }
    }


    private suspend fun <T> latestVersionReq(body: LatestVersionParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UpdateApiService::class.java)
                .fetchLatestVersion(body)) as T
    }


}