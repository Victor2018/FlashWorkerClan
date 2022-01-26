package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITalentJobDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface ITalentJobDS {
    val talentWaitEmployData: LiveData<HttpResult<TalentWaitEmployReq>>
    suspend fun fetchTalentWaitEmploy(token: String?,body: TalentWaitEmployParm?)

    val talentEmployingData: LiveData<HttpResult<TalentEmployingReq>>
    suspend fun fetchTalentEmploying(token: String?,body: TalentEmployingParm?)

    val talentWaitCommentData: LiveData<HttpResult<TalentWaitCommentReq>>
    suspend fun fetchTalentWaitComment(token: String?,body: TalentWaitCommentParm?)

    val talentJobFinishData: LiveData<HttpResult<TalentJobFinishReq>>
    suspend fun fetchTalentJobFinish(token: String?,body: TalentJobFinishParm?)

    val talentJobDeleteData: LiveData<HttpResult<BaseReq>>
    suspend fun talentJobDelete(token: String?,body: TalentJobDeleteParm?)

    val talentOrderDetailData: LiveData<HttpResult<TalentOrderDetailReq>>
    suspend fun fetchTalentOrderDetail(token: String?,jobOrderId: String?)

    val settlementDateData: LiveData<HttpResult<SettlementDateReq>>
    suspend fun fetchSettlementDate(token: String?,employerReleaseId: String?)

    val talentSettlementOrderData: LiveData<HttpResult<TalentSettlementOrderReq>>
    suspend fun fetchTalentSettlementOrder(token: String?,body: TalentSettlementOrderParm?)

    val cancelJobConfirmDetailData: LiveData<HttpResult<CancelJobConfirmDetailReq>>
    suspend fun fetchCancelJobConfirmDetail(token: String?,body: CancelJobConfirmDetailParm?)

    val cancelJobData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelJob(token: String?,body: CancelJobParm?)

    val arrivePostData: LiveData<HttpResult<BaseReq>>
    suspend fun arrivePost(token: String?,body: ArrivePostParm?)

    val finishJobData: LiveData<HttpResult<BaseReq>>
    suspend fun finishJob(token: String?,body: FinishJobParm?)

    val remindPrepaidData: LiveData<HttpResult<BaseReq>>
    suspend fun remindPrepaid(token: String?,body: RemindPrepaidParm?)

    val remindSettlementData: LiveData<HttpResult<BaseReq>>
    suspend fun remindSettlement(token: String?,body: RemindSettlementParm?)

    val unReadStatusData: LiveData<HttpResult<TalentUnReadStatusReq>>
    suspend fun fetchUnReadStatus(token: String?)

    val taskSettlementDetailData: LiveData<HttpResult<TaskSettlementDetailReq>>
    suspend fun fetchTaskSettlementDetail(token: String?,body: TaskSettlementDetailParm?)

    val taskSubmitData: LiveData<HttpResult<BaseReq>>
    suspend fun taskSubmit(token: String?,body: TaskSubmitParm?)

    val taskSubmitDetailData: LiveData<HttpResult<TaskSubmitDetailReq>>
    suspend fun fetchTaskSubmitDetail(token: String?,body: TaskSubmitDetailParm?)

}