package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ITalentReleaseDS
import com.flash.worker.lib.coremodel.http.service.TalentReleaseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseDS: AbsDS(),ITalentReleaseDS {

    override val saveTalentDraftsData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun saveTalentDrafts(token: String?,body: SaveTalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveTalentDraftsData.value = saveTalentDraftsReq(token,body)
        }
    }

    override val saveTalentReleaseData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun saveTalentRelease(token: String?,body: SaveTalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveTalentReleaseData.value = saveTalentReleaseReq(token,body)
        }
    }

    override val updateTalentDraftsData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun updateTalentDrafts(token: String?,body: UpdateTalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateTalentDraftsData.value = updateTalentDraftsReq(token,body)
        }
    }

    override val updateTalentReleaseData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun updateTalentRelease(token: String?,body: UpdateTalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateTalentReleaseData.value = updateTalentReleaseReq(token,body)
        }
    }

    override val talentReleaseData = MutableLiveData<HttpResult<TalentReleaseReq>>()

    override suspend fun fetchTalentRelease(token: String?, body: TalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseData.value = talentReleaseReq(token,body)
        }
    }

    override val talentReleaseRefreshData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun talentReleaseRefresh(token: String?, body: TalentReleaseRefreshParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseRefreshData.value = talentReleaseReFreshReq(token,body)
        }
    }

    override val talentReleaseOffShelfData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun talentReleaseOffShelf(token: String?, body: TalentReleaseOffShelfParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseOffShelfData.value = talentReleaseOffShelfReq(token,body)
        }
    }

    override val talentReleaseNewReleaseData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun talentReleaseNewRelease(token: String?, body: TalentReleaseNewReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseNewReleaseData.value = talentReleaseNewReleaseReq(token,body)
        }
    }

    override val talentReleaseDeleteData = MutableLiveData<HttpResult<BaseReq>>()

    override suspend fun talentReleaseDelete(token: String?, body: TalentReleaseDeleteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseDeleteData.value = talentReleaseDeleteReq(token,body)
        }
    }

    override val talentReleaseDetailData = MutableLiveData<HttpResult<TalentReleaseDetailReq>>()

    override suspend fun fetchTalentReleaseDetail(token: String?, releaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentReleaseDetailData.value = talentReleaseDetailReq(token,releaseId)
        }
    }

    private suspend fun <T> saveTalentDraftsReq(
            token: String?,
            body: SaveTalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .saveTalentDrafts(token,body)) as T
    }

    private suspend fun <T> saveTalentReleaseReq(
            token: String?,
            body: SaveTalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .saveTalentRelease(token,body)) as T
    }

    private suspend fun <T> updateTalentDraftsReq(
            token: String?,
            body: UpdateTalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .updateTalentDrafts(token,body)) as T
    }

    private suspend fun <T> updateTalentReleaseReq(
            token: String?,
            body: UpdateTalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .updateTalentRelease(token,body)) as T
    }

    private suspend fun <T> talentReleaseReq(
            token: String?,
            body: TalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .getTalentReleaseList(token,body)) as T
    }

    private suspend fun <T> talentReleaseReFreshReq(
            token: String?,
            body: TalentReleaseRefreshParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .talentReleaseRefresh(token,body)) as T
    }

    private suspend fun <T> talentReleaseOffShelfReq(
            token: String?,
            body: TalentReleaseOffShelfParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .talentReleaseOffShelf(token,body)) as T
    }

    private suspend fun <T> talentReleaseNewReleaseReq(
            token: String?,
            body: TalentReleaseNewReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .talentReleaseNewRelease(token,body)) as T
    }

    private suspend fun <T> talentReleaseDeleteReq(
            token: String?,
            body: TalentReleaseDeleteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .talentReleaseDelete(token,body)) as T
    }

    private suspend fun <T> talentReleaseDetailReq(
            token: String?,
            releaseId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentReleaseApiService::class.java)
                        .getTalentReleaseDetail(token,releaseId)) as T
    }


}