package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.HotKeywordParm
import com.flash.worker.lib.coremodel.data.parm.SearchEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTaskParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IHomeDS
import com.flash.worker.lib.coremodel.http.service.HomeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HomeDS
 * Author: Victor
 * Date: 2021/4/13 14:14
 * Description: 
 * -----------------------------------------------------------------
 */
class HomeDS: AbsDS(),IHomeDS {

    override val searchTalentReleaseData = MutableLiveData<HttpResult<SearchTalentReleaseReq>>()
    override suspend fun searchTalentRelease(body: SearchTalentReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            searchTalentReleaseData.value = searchTalentReleaseReq(body)
        }
    }

    override val searchEmployerReleaseData = MutableLiveData<HttpResult<SearchEmployerReleaseReq>>()
    override suspend fun searchEmployerRelease(body: SearchEmployerReleaseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            searchEmployerReleaseData.value = searchEmployerReleaseReq(body)
        }
    }

    override val homeTalentDetailData = MutableLiveData<HttpResult<HomeTalentDetailReq>>()
    override suspend fun fetchHomeTalentDetail(token: String?, releaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            homeTalentDetailData.value = homeTalentDetailReq(token,releaseId)
        }
    }

    override val homeEmployerDetailData = MutableLiveData<HttpResult<HomeEmployerDetailReq>>()
    override suspend fun fetchHomeEmployerDetail(token: String?,releaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            homeEmployerDetailData.value = homeEmployerDetailReq(token,releaseId)
        }
    }

    override val hotKeywordData = MutableLiveData<HttpResult<HotKeywordReq>>()
    override suspend fun fetchHotKeyword(token: String?,body: HotKeywordParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            hotKeywordData.value = hotKeywordReq(token,body)
        }
    }

    override val bannerData = MutableLiveData<HttpResult<BannerReq>>()
    override suspend fun fetchBanner(bannerType: Int) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            bannerData.value = bannerReq(bannerType)
        }
    }

    override val announcementData = MutableLiveData<HttpResult<AnnouncementReq>>()
    override suspend fun fetchAnnouncement() {
        // Force Main thread
        withContext(Dispatchers.Main) {
            announcementData.value = announcementReq()
        }
    }

    override val searchTaskData = MutableLiveData<HttpResult<SearchTaskReq>>()
    override suspend fun searchTask(token: String?,body: SearchTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            searchTaskData.value = searchTaskReq(token,body)
        }
    }

    override val taskDetailData = MutableLiveData<HttpResult<TaskDetailReq>>()
    override suspend fun fetchTaskDetail(token: String?,releaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskDetailData.value = taskDetailReq(token,releaseId)
        }
    }

    private suspend fun <T> searchTalentReleaseReq(
            body: SearchTalentReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .searchTalentRelease(body)) as T
    }

    private suspend fun <T> searchEmployerReleaseReq(
            body: SearchEmployerReleaseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .searchEmployerRelease(body)) as T
    }

    private suspend fun <T> homeTalentDetailReq(
            token: String?,
            releaseId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchHomeTalentDetail(token,releaseId)) as T
    }

    private suspend fun <T> homeEmployerDetailReq(
            token: String?,
            releaseId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchHomeEmployerDetail(token,releaseId)) as T
    }

    private suspend fun <T> hotKeywordReq(
            token: String?,
            body: HotKeywordParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchHotKeyword(token,body)) as T
    }

    private suspend fun <T> bannerReq(bannerType: Int): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchBanner(bannerType)) as T
    }

    private suspend fun <T> announcementReq(): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchAnnouncement()) as T
    }

    private suspend fun <T> searchTaskReq(token: String?,body: SearchTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .searchTask(token,body)) as T
    }

    private suspend fun <T> taskDetailReq(
        token: String?,
        releaseId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(HomeApiService::class.java)
                .fetchTaskDetail(token,releaseId)) as T
    }

}