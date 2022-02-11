package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmployerJobDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmployerJobDS {
    val employerWaitEmployData: LiveData<HttpResult<EmployerWaitEmployReq>>
    suspend fun fetchWaitEmploy(token: String?,body: EmployerWaitEmployParm?)

    val employerSignUpUserData: LiveData<HttpResult<EmployerSignUpUserReq>>
    suspend fun fetchEmployerSignUpUser(token: String?,body: EmployerSignUpUserParm?)

    val employerCancelledData: LiveData<HttpResult<EmployerCancelledReq>>
    suspend fun fetchEmployerCancelled(token: String?,body: EmployerCancelledParm?)

    val employerJobDeleteData: LiveData<HttpResult<BaseReq>>
    suspend fun employerJobDelete(token: String?,body: EmployerJobDeleteParm?)

    val talentUserData: LiveData<HttpResult<TalentUserReq>>
    suspend fun fetchTalentUser(token: String?,body: TalentUserParm?)

    val employmentNumData: LiveData<HttpResult<EmploymentNumReq>>
    suspend fun fetchEmploymentNum(token: String?,employerReleaseId: String?)

    val employerEmployingData: LiveData<HttpResult<EmployerEmployingReq>>
    suspend fun fetchEmployerEmploying(token: String?,body: EmployerEmployingParm?)

    val employerSettlementOrderData: LiveData<HttpResult<EmployerSettlementOrderReq>>
    suspend fun fetchEmployerSettlementOrder(token: String?,body: EmployerSettlementOrderParm?)

    val settlementNumData: LiveData<HttpResult<SettlementNumReq>>
    suspend fun fetchSettlementNum(token: String?,body: SettlementNumParm?)

    val fireTalentConfirmDetailData: LiveData<HttpResult<FireTalentConfirmDetailReq>>
    suspend fun fetchFireTalentConfirmDetail(token: String?,body: FireTalentConfirmDetailParm?)

    val fireTalentData: LiveData<HttpResult<BaseReq>>
    suspend fun fireTalent(token: String?,body: FireTalentParm?)

    val employerWaitCommentData: LiveData<HttpResult<EmployerWaitCommentReq>>
    suspend fun fetchEmployerWaitComment(token: String?,body: EmployerWaitCommentParm?)

    val employerJobFinishData: LiveData<HttpResult<EmployerJobFinishReq>>
    suspend fun fetchEmployerJobFinish(token: String?,body: EmployerJobFinishParm?)

    val employerOrderDeleteData: LiveData<HttpResult<BaseReq>>
    suspend fun employerOrderDelete(token: String?,body: EmployerOrderDeleteParm?)

    val employerFinishUserData: LiveData<HttpResult<EmployerFinishUserReq>>
    suspend fun fetchEmployerFinishUser(token: String?,body: EmployerFinishUserParm?)

    val employerWaitCommentUserData: LiveData<HttpResult<EmployerWaitCommentUserReq>>
    suspend fun fetchEmployerWaitCommentUser(token: String?,body: EmployerWaitCommentUserParm?)

    val employerReleasingData: LiveData<HttpResult<EmployerReleasingReq>>
    suspend fun fetchEmployerReleasing(token: String?,body: EmployerReleasingParm?)

    val unReadStatusData: LiveData<HttpResult<EmployerUnReadStatusReq>>
    suspend fun fetchUnReadStatus(token: String?)

    val taskSettledData: LiveData<HttpResult<TaskSettledReq>>
    suspend fun fetchTaskSettled(token: String?,body: TaskSettledParm?)

    val checkAutoPrepaidData: LiveData<HttpResult<CheckAutoPrepaidReq>>
    suspend fun checkAutoPrepaid(token: String?,body: CheckAutoPrepaidParm?)

    val openAutoPrepaidData: LiveData<HttpResult<BaseReq>>
    suspend fun openAutoPrepaid(token: String?,body: OpenAutoPrepaidParm?)

    val closeAutoPrepaidData: LiveData<HttpResult<BaseReq>>
    suspend fun closeAutoPrepaid(token: String?,body: CloseAutoPrepaidParm?)

}