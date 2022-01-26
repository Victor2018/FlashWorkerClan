package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.BindAliPayParm
import com.flash.worker.lib.coremodel.data.parm.OpenUserUnBindParm
import com.flash.worker.lib.coremodel.data.req.AliAuthReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.OpenUserInfoReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IOpenUserDS
 * Author: Victor
 * Date: 2021/4/13 14:25
 * Description: 
 * -----------------------------------------------------------------
 */
interface IOpenUserDS {
    val bindAliPayData: LiveData<HttpResult<BaseReq>>
    suspend fun bindAliPay(token: String?,body: BindAliPayParm?)

    val aliAuthData: LiveData<HttpResult<AliAuthReq>>
    suspend fun fetchAliAuthInfo(token: String?)

    val openUserInfoData: LiveData<HttpResult<OpenUserInfoReq>>
    suspend fun fetchOpenUserInfo(token: String?)

    val openUserUnBindData: LiveData<HttpResult<BaseReq>>
    suspend fun openUserUnBind(token: String?,body: OpenUserUnBindParm?)

}