package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ITalentFavReleaseDS
import com.flash.worker.lib.coremodel.http.service.TalentFavApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavReleaseDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavReleaseDS: AbsDS(),ITalentFavReleaseDS {

    override val addFavData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun addFavRelease(token: String?,body: TalentAddFavReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            addFavData.value = addFavReleaseReq(token,body)
        }
    }

    override val cancelFavData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelFavRelease(token: String?,body: TalentCancelFavReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelFavData.value = cancelFavReleaseReq(token,body)
        }
    }

    override val favReleaseData = MutableLiveData<HttpResult<TalentFavReleaseReq>>()
    override suspend fun fetchFavRelease(token: String?,body: TalentFavReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            favReleaseData.value = favReleaseReq(token,body)
        }
    }

    private suspend fun <T> addFavReleaseReq(
            token: String?,body: TalentAddFavReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentFavApiService::class.java)
                .addFavRelease(token,body)) as T
    }

    private suspend fun <T> cancelFavReleaseReq(
            token: String?,body: TalentCancelFavReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentFavApiService::class.java)
                .cancelFavRelease(token,body)) as T
    }

    private suspend fun <T> favReleaseReq(
            token: String?,body: TalentFavReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentFavApiService::class.java)
                .fetchFavRelease(token,body)) as T
    }

}