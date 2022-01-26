package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ITalentJobDS
import com.flash.worker.lib.coremodel.http.service.TalentJobApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobDS: AbsDS(), ITalentJobDS {

    override val talentWaitEmployData = MutableLiveData<HttpResult<TalentWaitEmployReq>>()
    override suspend fun fetchTalentWaitEmploy(token: String?,body: TalentWaitEmployParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentWaitEmployData.value = talentWaitEmployReq(token,body)
        }
    }

    override val talentEmployingData = MutableLiveData<HttpResult<TalentEmployingReq>>()
    override suspend fun fetchTalentEmploying(token: String?,body: TalentEmployingParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentEmployingData.value = talentEmployingReq(token,body)
        }
    }

    override val talentWaitCommentData = MutableLiveData<HttpResult<TalentWaitCommentReq>>()
    override suspend fun fetchTalentWaitComment(token: String?,body: TalentWaitCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentWaitCommentData.value = talentWaitCommentReq(token,body)
        }
    }

    override val talentJobFinishData = MutableLiveData<HttpResult<TalentJobFinishReq>>()
    override suspend fun fetchTalentJobFinish(token: String?,body: TalentJobFinishParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentJobFinishData.value = talentJobFinishReq(token,body)
        }
    }

    override val talentJobDeleteData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun talentJobDelete(token: String?,body: TalentJobDeleteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentJobDeleteData.value = talentJobDeleteReq(token,body)
        }
    }

    override val talentOrderDetailData = MutableLiveData<HttpResult<TalentOrderDetailReq>>()
    override suspend fun fetchTalentOrderDetail(token: String?,jobOrderId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentOrderDetailData.value = talentOrderDetailReq(token,jobOrderId)
        }
    }

    override val settlementDateData = MutableLiveData<HttpResult<SettlementDateReq>>()
    override suspend fun fetchSettlementDate(token: String?,employerReleaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            settlementDateData.value = settlementDateReq(token,employerReleaseId)
        }
    }

    override val talentSettlementOrderData = MutableLiveData<HttpResult<TalentSettlementOrderReq>>()
    override suspend fun fetchTalentSettlementOrder(token: String?,body: TalentSettlementOrderParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentSettlementOrderData.value = talentSettlementOrderReq(token,body)
        }
    }

    override val cancelJobConfirmDetailData = MutableLiveData<HttpResult<CancelJobConfirmDetailReq>>()
    override suspend fun fetchCancelJobConfirmDetail(token: String?,body: CancelJobConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelJobConfirmDetailData.value = cancelJobConfirmDetailReq(token,body)
        }
    }

    override val cancelJobData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelJob(token: String?,body: CancelJobParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelJobData.value = cancelJobReq(token,body)
        }
    }

    override val arrivePostData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun arrivePost(token: String?,body: ArrivePostParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            arrivePostData.value = arrivePostReq(token,body)
        }
    }

    override val finishJobData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun finishJob(token: String?,body: FinishJobParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            finishJobData.value = finishJobReq(token,body)
        }
    }

    override val remindPrepaidData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun remindPrepaid(token: String?,body: RemindPrepaidParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            remindPrepaidData.value = remindPrepaidReq(token,body)
        }
    }

    override val remindSettlementData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun remindSettlement(token: String?,body: RemindSettlementParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            remindSettlementData.value = remindSettlementReq(token,body)
        }
    }

    override val unReadStatusData = MutableLiveData<HttpResult<TalentUnReadStatusReq>>()
    override suspend fun fetchUnReadStatus(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            unReadStatusData.value = unReadStatusReq(token)
        }
    }

    override val taskSettlementDetailData = MutableLiveData<HttpResult<TaskSettlementDetailReq>>()
    override suspend fun fetchTaskSettlementDetail(token: String?,body: TaskSettlementDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSettlementDetailData.value = taskSettlementDetailReq(token,body)
        }
    }

    override val taskSubmitData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun taskSubmit(token: String?,body: TaskSubmitParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSubmitData.value = taskSubmitReq(token,body)
        }
    }

    override val taskSubmitDetailData = MutableLiveData<HttpResult<TaskSubmitDetailReq>>()
    override suspend fun fetchTaskSubmitDetail(token: String?,body: TaskSubmitDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSubmitDetailData.value = taskSubmitDetailReq(token,body)
        }
    }

    private suspend fun <T> talentWaitEmployReq(
        token: String?,
        body: TalentWaitEmployParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentWaitEmploy(token,body)) as T
    }

    private suspend fun <T> talentEmployingReq(
        token: String?,
        body: TalentEmployingParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentEmploying(token,body)) as T
    }

    private suspend fun <T> talentWaitCommentReq(
        token: String?,
        body: TalentWaitCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentWaitComment(token,body)) as T
    }

    private suspend fun <T> talentJobFinishReq(
        token: String?,
        body: TalentJobFinishParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentJobFinish(token,body)) as T
    }



    private suspend fun <T> talentJobDeleteReq(
        token: String?,
        body: TalentJobDeleteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .talentJobDelete(token,body)) as T
    }

    private suspend fun <T> talentOrderDetailReq(
        token: String?,
        jobOrderId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentOrderDetail(token,jobOrderId)) as T
    }

    private suspend fun <T> settlementDateReq(
        token: String?,
        employerReleaseId : String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchSettlementDate(token,employerReleaseId)) as T
    }

    private suspend fun <T> talentSettlementOrderReq(
        token: String?,
        body: TalentSettlementOrderParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTalentSettlementOrder(token,body)) as T
    }

    private suspend fun <T> cancelJobConfirmDetailReq(
        token: String?,
        body: CancelJobConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchCancelJobConfirmDetail(token,body)) as T
    }

    private suspend fun <T> cancelJobReq(
        token: String?,
        body: CancelJobParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .cancelJob(token,body)) as T
    }

    private suspend fun <T> arrivePostReq(
        token: String?,
        body: ArrivePostParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .arrivePost(token,body)) as T
    }

    private suspend fun <T> finishJobReq(
        token: String?,
        body: FinishJobParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .finishJob(token,body)) as T
    }

    private suspend fun <T> remindPrepaidReq(
        token: String?,
        body: RemindPrepaidParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .remindPrepaid(token,body)) as T
    }

    private suspend fun <T> remindSettlementReq(
        token: String?,
        body: RemindSettlementParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .remindSettlement(token,body)) as T
    }

    private suspend fun <T> unReadStatusReq(
        token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchUnReadStatus(token)) as T
    }

    private suspend fun <T> taskSettlementDetailReq(
        token: String?,
        body: TaskSettlementDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTaskSettlementDetail(token,body)) as T
    }

    private suspend fun <T> taskSubmitReq(
        token: String?,
        body: TaskSubmitParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .taskSubmit(token,body)) as T
    }

    private suspend fun <T> taskSubmitDetailReq(
        token: String?,
        body: TaskSubmitDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TalentJobApiService::class.java)
                        .fetchTaskSubmitDetail(token,body)) as T
    }

}