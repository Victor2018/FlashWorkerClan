package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IAccountDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IGuildRedEnvelopeDS {

    val redEnvelopeInfoData: LiveData<HttpResult<GuildRedEnvelopeInfoReq>>
    suspend fun fetchRedEnvelopeInfo(token: String?)

    val receiveRedEnvelopeData: LiveData<HttpResult<BaseReq>>
    suspend fun receiveRedEnvelope(token: String?)

    val guildRedEnvelopeTipsData: LiveData<HttpResult<GuildRedEnvelopeTipsReq>>
    suspend fun fetchGuildRedEnvelopeTips(token: String?)

}