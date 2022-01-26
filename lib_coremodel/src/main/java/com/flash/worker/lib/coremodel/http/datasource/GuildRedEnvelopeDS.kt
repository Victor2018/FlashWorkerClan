package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import com.flash.worker.lib.coremodel.http.interfaces.IGuildRedEnvelopeDS
import com.flash.worker.lib.coremodel.http.service.AccountApiService
import com.flash.worker.lib.coremodel.http.service.GuildRedEnvelopeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildRedEnvelopeDS: AbsDS(), IGuildRedEnvelopeDS {

    override val redEnvelopeInfoData = MutableLiveData<HttpResult<GuildRedEnvelopeInfoReq>>()
    override suspend fun fetchRedEnvelopeInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            redEnvelopeInfoData.value = redEnvelopeInfoReq(token)
        }
    }

    override val receiveRedEnvelopeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun receiveRedEnvelope(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveRedEnvelopeData.value = receiveRedEnvelopeReq(token)
        }
    }

    override val guildRedEnvelopeTipsData = MutableLiveData<HttpResult<GuildRedEnvelopeTipsReq>>()
    override suspend fun fetchGuildRedEnvelopeTips(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildRedEnvelopeTipsData.value = guildRedEnvelopeTipsReq(token)
        }
    }


    private suspend fun <T> redEnvelopeInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildRedEnvelopeApiService::class.java)
                .fetchRedEnvelopeInfo(token)) as T
    }

    private suspend fun <T> receiveRedEnvelopeReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildRedEnvelopeApiService::class.java)
                .receiveRedEnvelope(token)) as T
    }

    private suspend fun <T> guildRedEnvelopeTipsReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(GuildRedEnvelopeApiService::class.java)
                .guildRedEnvelopeTips(token)) as T
    }


}