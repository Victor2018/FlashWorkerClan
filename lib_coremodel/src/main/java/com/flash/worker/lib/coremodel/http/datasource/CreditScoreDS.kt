package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ICreditScoreDS
import com.flash.worker.lib.coremodel.http.service.CreditScoreApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditScoreDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class CreditScoreDS: AbsDS(),ICreditScoreDS {

    override val talentCreditScoreData = MutableLiveData<HttpResult<CreditScoreReq>>()
    override suspend fun fetchTalentCreditScore(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentCreditScoreData.value = talentCreditScoreReq(token)
        }
    }

    override val employerCreditScoreData = MutableLiveData<HttpResult<CreditScoreReq>>()
    override suspend fun fetchEmployerCreditScore(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerCreditScoreData.value = employerCreditScoreReq(token)
        }
    }

    private suspend fun <T> talentCreditScoreReq(
            token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CreditScoreApiService::class.java)
                .fetchTalentCreditScore(token)) as T
    }

    private suspend fun <T> employerCreditScoreReq(
            token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CreditScoreApiService::class.java)
                .fetchEmployerCreditScore(token)) as T
    }


}