package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.PayParm
import com.flash.worker.lib.coremodel.data.parm.PayQueryParm
import com.flash.worker.lib.coremodel.data.req.PayQueryReq
import com.flash.worker.lib.coremodel.data.req.PayReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IPayDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IPayDS {
    val payData: LiveData<HttpResult<PayReq>>
    suspend fun pay(token: String?,body: PayParm?)

    val payQueryData: LiveData<HttpResult<PayQueryReq>>
    suspend fun payQuery(token: String?,body: PayQueryParm?)
}