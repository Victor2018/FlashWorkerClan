package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ITalentRedEnvelopeDS
import com.flash.worker.lib.coremodel.http.service.TalentRedEnvelopeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentRedEnvelopeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentRedEnvelopeDS: AbsDS(), ITalentRedEnvelopeDS {

    override val activitytInfoData = MutableLiveData<HttpResult<ActivityInfoReq>>()
    override suspend fun fetchActivityInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            activitytInfoData.value = activityInfoReq(token)
        }
    }

    override val receiveTalentRedEnvelopeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun receiveTalentRedEnvelope(token: String?,body: ReceiveTalentRedEnvelopeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveTalentRedEnvelopeData.value = receiveTalentRedEnvelopeReq(token,body)
        }
    }

    override val waitReceiveCountData = MutableLiveData<HttpResult<WaitReceiveCountReq>>()
    override suspend fun fetchWaitReceiveCount(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            waitReceiveCountData.value = waitReceiveCountReq(token)
        }
    }


    private suspend fun <T> activityInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentRedEnvelopeApiService::class.java)
                        .fetchActivityInfo(token)) as T
    }

    private suspend fun <T> receiveTalentRedEnvelopeReq(token: String?,body: ReceiveTalentRedEnvelopeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentRedEnvelopeApiService::class.java)
                        .receiveTalentRedEnvelope(token,body)) as T
    }

    private suspend fun <T> waitReceiveCountReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentRedEnvelopeApiService::class.java)
                .fetchWaitReceiveCount(token)) as T
    }


}