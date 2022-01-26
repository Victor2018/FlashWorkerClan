package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.TalentReleaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITalentReleaseDS
 * Author: Victor
 * Date: 2020/12/23 15:40
 * Description: 
 * -----------------------------------------------------------------
 */
interface ITalentReleaseDS {
    val saveTalentDraftsData: LiveData<HttpResult<BaseReq>>
    suspend fun saveTalentDrafts(token: String?,body: SaveTalentReleaseParm?)

    val saveTalentReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun saveTalentRelease(token: String?,body: SaveTalentReleaseParm?)

    val updateTalentDraftsData: LiveData<HttpResult<BaseReq>>
    suspend fun updateTalentDrafts(token: String?,body: UpdateTalentReleaseParm?)

    val updateTalentReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun updateTalentRelease(token: String?,body: UpdateTalentReleaseParm?)

    val talentReleaseData: LiveData<HttpResult<TalentReleaseReq>>
    suspend fun fetchTalentRelease(token: String?,body: TalentReleaseParm?)

    val talentReleaseRefreshData: LiveData<HttpResult<BaseReq>>
    suspend fun talentReleaseRefresh(token: String?,body: TalentReleaseRefreshParm?)

    val talentReleaseOffShelfData: LiveData<HttpResult<BaseReq>>
    suspend fun talentReleaseOffShelf(token: String?,body: TalentReleaseOffShelfParm?)

    val talentReleaseNewReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun talentReleaseNewRelease(token: String?,body: TalentReleaseNewReleaseParm?)

    val talentReleaseDeleteData: LiveData<HttpResult<BaseReq>>
    suspend fun talentReleaseDelete(token: String?,body: TalentReleaseDeleteParm?)

    val talentReleaseDetailData: LiveData<HttpResult<TalentReleaseDetailReq>>
    suspend fun fetchTalentReleaseDetail(token: String?,releaseId: String?)

}