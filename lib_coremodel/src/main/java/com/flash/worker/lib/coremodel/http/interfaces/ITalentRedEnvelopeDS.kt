package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITalentRedEnvelopeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface ITalentRedEnvelopeDS {

    val activitytInfoData: LiveData<HttpResult<ActivityInfoReq>>
    suspend fun fetchActivityInfo(token: String?)

    val receiveTalentRedEnvelopeData: LiveData<HttpResult<BaseReq>>
    suspend fun receiveTalentRedEnvelope(token: String?,body: ReceiveTalentRedEnvelopeParm?)

    val waitReceiveCountData: LiveData<HttpResult<WaitReceiveCountReq>>
    suspend fun fetchWaitReceiveCount(token: String?)

}