package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ICommonDS
import com.flash.worker.lib.coremodel.http.service.CommonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommonDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class CommonDS:AbsDS(),ICommonDS {

    override val areaTreeData = MutableLiveData<HttpResult<AreaTreeReq>>()
    override suspend fun fetchAreaTreeData(body: AreaTreeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            areaTreeData.value = areaTreeDataReq(body)
        }
    }

    override val talentTypeData = MutableLiveData<HttpResult<TalentTypeReq>>()
    override suspend fun fetchTalentTypeData() {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentTypeData.value = talentTypeReq()
        }
    }

    override val cityAreaData = MutableLiveData<HttpResult<CityAreaReq>>()
    override suspend fun fetchCityAreaData(token: String?,city: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cityAreaData.value = cityAreaReq(token,city)
        }
    }


    override val systemTimeData = MutableLiveData<HttpResult<SystemTimeReq>>()
    override suspend fun fetchSystemTime(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            systemTimeData.value = systemTimeReq(token)
        }
    }

    override val rewardLabelData = MutableLiveData<HttpResult<RewardLabelReq>>()
    override suspend fun fetchRewardLabel(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            rewardLabelData.value = rewardLabelReq(token)
        }
    }

    override val talentViolationLabelData = MutableLiveData<HttpResult<ViolationLabelReq>>()
    override suspend fun fetchTalentViolationLabel(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentViolationLabelData.value = talentViolationLabelReq(token)
        }
    }

    override val employerViolationLabelData = MutableLiveData<HttpResult<ViolationLabelReq>>()
    override suspend fun fetchEmployerViolationLabel(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerViolationLabelData.value = employerViolationLabelReq(token)
        }
    }

    private suspend fun <T> areaTreeDataReq(body: AreaTreeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                .getAreaTree(body)) as T
    }

    private suspend fun <T> talentTypeReq(): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                .getTalentType()) as T
    }

    private suspend fun <T> cityAreaReq(token: String?,city: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                .fetchCityArea(token,city)) as T
    }

    private suspend fun <T> systemTimeReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                        .fetchSystemTime(token)) as T
    }

    private suspend fun <T> rewardLabelReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                        .fetchRewardLabel(token)) as T
    }

    private suspend fun <T> talentViolationLabelReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                        .fetchTalentViolationLabel(token)) as T
    }

    private suspend fun <T> employerViolationLabelReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommonApiService::class.java)
                        .fetchEmployerViolationLabel(token)) as T
    }


}