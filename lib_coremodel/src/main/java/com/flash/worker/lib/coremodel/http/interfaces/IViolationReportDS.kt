package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReportEmployerViolationParm
import com.flash.worker.lib.coremodel.data.parm.ReportTalentViolationParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ViolationUserReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IViolationReportDS
 * Author: Victor
 * Date: 2021/7/15 17:27
 * Description: 
 * -----------------------------------------------------------------
 */
interface IViolationReportDS {

    val reportTalentViolationData: LiveData<HttpResult<BaseReq>>
    suspend fun reportTalentViolation(token: String?,body: ReportTalentViolationParm?)

    val reportEmployerViolationData: LiveData<HttpResult<BaseReq>>
    suspend fun reportEmployerViolation(token: String?,body: ReportEmployerViolationParm?)

    val violationUserData: LiveData<HttpResult<ViolationUserReq>>
    suspend fun fetchViolationUser(token: String?)
}