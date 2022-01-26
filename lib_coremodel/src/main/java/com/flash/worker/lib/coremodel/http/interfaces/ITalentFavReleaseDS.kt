package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentAddFavReleaseParm
import com.flash.worker.lib.coremodel.data.parm.TalentCancelFavReleaseParm
import com.flash.worker.lib.coremodel.data.parm.TalentFavReleaseParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TalentFavReleaseReq


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
interface ITalentFavReleaseDS {
    val addFavData: LiveData<HttpResult<BaseReq>>
    suspend fun addFavRelease(token: String?,body: TalentAddFavReleaseParm?)

    val cancelFavData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelFavRelease(token: String?,body: TalentCancelFavReleaseParm?)

    val favReleaseData: LiveData<HttpResult<TalentFavReleaseReq>>
    suspend fun fetchFavRelease(token: String?,body: TalentFavReleaseParm?)

}