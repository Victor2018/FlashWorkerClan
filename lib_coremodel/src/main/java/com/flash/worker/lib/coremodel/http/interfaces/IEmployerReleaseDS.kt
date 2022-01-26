package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseReq
import com.flash.worker.lib.coremodel.data.req.InviteTalentEmployerReleaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmployerReleaseDS
 * Author: Victor
 * Date: 2021/4/13 11:00
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmployerReleaseDS {
    val saveEmployerDraftsData: LiveData<HttpResult<BaseReq>>
    suspend fun saveEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?)

    val saveEmployerReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun saveEmployerRelease(token: String?,body: SaveEmployerReleaseParm?)

    val employerReleaseData: LiveData<HttpResult<EmployerReleaseReq>>
    suspend fun fetchEmployerRelease(token: String?,body: EmployerReleaseParm?)

    val employerReleaseRefreshData: LiveData<HttpResult<BaseReq>>
    suspend fun employerReleaseRefresh(token: String?,body: EmployerReleaseRefreshParm?)

    val employerReleaseOffShelfData: LiveData<HttpResult<BaseReq>>
    suspend fun employerReleaseOffShelf(token: String?,body: EmployerReleaseOffShelfParm?)

    val employerReleaseNewReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun employerReleaseNewRelease(token: String?,body: EmployerReleaseNewReleaseParm?)

    val employerReleaseDeleteData: LiveData<HttpResult<BaseReq>>
    suspend fun employerReleaseDelete(token: String?,body: EmployerReleaseDeleteParm?)

    val employerReleaseDetailData: LiveData<HttpResult<EmployerReleaseDetailReq>>
    suspend fun fetchEmployerReleaseDetail(token: String?,releaseId : String?)

    val updateEmployerDraftsData: LiveData<HttpResult<BaseReq>>
    suspend fun updateEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?)

    val updateEmployerReleaseData: LiveData<HttpResult<BaseReq>>
    suspend fun updateEmployerRelease(token: String?,body: SaveEmployerReleaseParm?)

    val inviteTalentEmployerReleaseData: LiveData<HttpResult<InviteTalentEmployerReleaseReq>>
    suspend fun fetchInviteTalentEmployerRelease(token: String?,body: InviteTalentEmployerReleaseParm?)
}