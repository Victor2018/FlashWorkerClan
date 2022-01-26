package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerFavReleaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITalentFavReleaseDS
 * Author: Victor
 * Date: 2021/4/23 17:18
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmployerFavReleaseDS {
    val addFavData: LiveData<HttpResult<BaseReq>>
    suspend fun addFavRelease(token: String?,body: EmployerAddFavReleaseParm?)

    val cancelFavData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelFavRelease(token: String?,body: EmployerCancelFavReleaseParm?)

    val favReleaseData: LiveData<HttpResult<EmployerFavReleaseReq>>
    suspend fun fetchFavRelease(token: String?,body: EmployerFavReleaseParm?)

}