package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerJobDS
import com.flash.worker.lib.coremodel.http.service.EmployerJobApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobDS: AbsDS(), IEmployerJobDS {

    override val employerWaitEmployData = MutableLiveData<HttpResult<EmployerWaitEmployReq>>()
    override suspend fun fetchWaitEmploy(token: String?,body: EmployerWaitEmployParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerWaitEmployData.value = employerWaitEmployReq(token,body)
        }
    }

    override val employerSignUpUserData = MutableLiveData<HttpResult<EmployerSignUpUserReq>>()
    override suspend fun fetchEmployerSignUpUser(token: String?,body: EmployerSignUpUserParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerSignUpUserData.value = employerSignUpUserReq(token,body)
        }
    }

    override val employerCancelledData = MutableLiveData<HttpResult<EmployerCancelledReq>>()
    override suspend fun fetchEmployerCancelled(token: String?,body: EmployerCancelledParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerCancelledData.value = fetchEmployerCancelledReq(token,body)
        }
    }

    override val employerJobDeleteData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerJobDelete(token: String?,body: EmployerJobDeleteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerJobDeleteData.value = employerJobDeleteReq(token,body)
        }
    }

    override val talentUserData = MutableLiveData<HttpResult<TalentUserReq>>()
    override suspend fun fetchTalentUser(token: String?,body: TalentUserParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentUserData.value = talentUserReq(token,body)
        }
    }

    override val employmentNumData = MutableLiveData<HttpResult<EmploymentNumReq>>()
    override suspend fun fetchEmploymentNum(token: String?,employerReleaseId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employmentNumData.value = employmentNumReq(token,employerReleaseId)
        }
    }

    override val employerEmployingData = MutableLiveData<HttpResult<EmployerEmployingReq>>()
    override suspend fun fetchEmployerEmploying(token: String?,body: EmployerEmployingParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerEmployingData.value = employerEmployingReq(token,body)
        }
    }

    override val employerSettlementOrderData = MutableLiveData<HttpResult<EmployerSettlementOrderReq>>()
    override suspend fun fetchEmployerSettlementOrder(token: String?,body: EmployerSettlementOrderParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerSettlementOrderData.value = employerSettlementOrderReq(token,body)
        }
    }

    override val settlementNumData = MutableLiveData<HttpResult<SettlementNumReq>>()
    override suspend fun fetchSettlementNum(token: String?,body: SettlementNumParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            settlementNumData.value = settlementNumReq(token,body)
        }
    }

    override val fireTalentConfirmDetailData = MutableLiveData<HttpResult<FireTalentConfirmDetailReq>>()
    override suspend fun fetchFireTalentConfirmDetail(token: String?,body: FireTalentConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            fireTalentConfirmDetailData.value = fireTalentConfirmDetailReq(token,body)
        }
    }

    override val fireTalentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun fireTalent(token: String?,body: FireTalentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            fireTalentData.value = fireTalentReq(token,body)
        }
    }

    override val employerWaitCommentData = MutableLiveData<HttpResult<EmployerWaitCommentReq>>()
    override suspend fun fetchEmployerWaitComment(token: String?,body: EmployerWaitCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerWaitCommentData.value = employerWaitCommentReq(token,body)
        }
    }

    override val employerJobFinishData = MutableLiveData<HttpResult<EmployerJobFinishReq>>()
    override suspend fun fetchEmployerJobFinish(token: String?,body: EmployerJobFinishParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerJobFinishData.value = employerJobFinishReq(token,body)
        }
    }

    override val employerOrderDeleteData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerOrderDelete(token: String?,body: EmployerOrderDeleteParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerOrderDeleteData.value = employerOrderDeleteReq(token,body)
        }
    }

    override val employerFinishUserData = MutableLiveData<HttpResult<EmployerFinishUserReq>>()
    override suspend fun fetchEmployerFinishUser(token: String?,body: EmployerFinishUserParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerFinishUserData.value = fetchEmployerFinishUserReq(token,body)
        }
    }

    override val employerWaitCommentUserData = MutableLiveData<HttpResult<EmployerWaitCommentUserReq>>()
    override suspend fun fetchEmployerWaitCommentUser(token: String?,body: EmployerWaitCommentUserParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerWaitCommentUserData.value = employerWaitCommentUserReq(token,body)
        }
    }

    override val employerReleasingData = MutableLiveData<HttpResult<EmployerReleasingReq>>()
    override suspend fun fetchEmployerReleasing(token: String?,body: EmployerReleasingParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerReleasingData.value = employerReleasingReq(token,body)
        }
    }

    override val unReadStatusData = MutableLiveData<HttpResult<EmployerUnReadStatusReq>>()
    override suspend fun fetchUnReadStatus(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            unReadStatusData.value = unReadStatusReq(token)
        }
    }

    override val taskSettledData = MutableLiveData<HttpResult<TaskSettledReq>>()
    override suspend fun fetchTaskSettled(token: String?,body: TaskSettledParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSettledData.value = fetchTaskSettledReq(token,body)
        }
    }

    override val checkAutoPrepaidData = MutableLiveData<HttpResult<CheckAutoPrepaidReq>>()
    override suspend fun checkAutoPrepaid(token: String?,body: CheckAutoPrepaidParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkAutoPrepaidData.value = checkAutoPrepaidReq(token,body)
        }
    }

    override val openAutoPrepaidData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun openAutoPrepaid(token: String?,body: OpenAutoPrepaidParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            openAutoPrepaidData.value = openAutoPrepaidReq(token,body)
        }
    }

    override val closeAutoPrepaidData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun closeAutoPrepaid(token: String?,body: CloseAutoPrepaidParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            closeAutoPrepaidData.value = closeAutoPrepaidReq(token,body)
        }
    }

    private suspend fun <T> employerWaitEmployReq(
        token: String?,
        body: EmployerWaitEmployParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                        .fetchEmployerWaitEmploy(token,body)) as T
    }

    private suspend fun <T> employerSignUpUserReq(
        token: String?,
        body: EmployerSignUpUserParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                        .fetchEmployerSignUpUser(token,body)) as T
    }

    private suspend fun <T> fetchEmployerCancelledReq(
        token: String?,
        body: EmployerCancelledParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                        .fetchEmployerCancelled(token,body)) as T
    }

    private suspend fun <T> employerJobDeleteReq(
        token: String?,
        body: EmployerJobDeleteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                        .employerJobDelete(token,body)) as T
    }

    private suspend fun <T> talentUserReq(
        token: String?,
        body: TalentUserParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                        .fetchTalentUser(token,body)) as T
    }

    private suspend fun <T> employmentNumReq(
            token: String?,
            employerReleaseId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmploymentNum(token,employerReleaseId)) as T
    }

    private suspend fun <T> employerEmployingReq(
            token: String?,
            body: EmployerEmployingParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmployerEmploying(token,body)) as T
    }

    private suspend fun <T> employerSettlementOrderReq(
            token: String?,
            body: EmployerSettlementOrderParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmployerSettlementOrder(token,body)) as T
    }

    private suspend fun <T> settlementNumReq(
            token: String?,
            body: SettlementNumParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchSettlementNum(token,body)) as T
    }

    private suspend fun <T> fireTalentConfirmDetailReq(
            token: String?,
            body: FireTalentConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fireTalentConfirmDetail(token,body)) as T
    }

    private suspend fun <T> fireTalentReq(
            token: String?,
            body: FireTalentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fireTalent(token,body)) as T
    }

    private suspend fun <T> employerWaitCommentReq(
            token: String?,
            body: EmployerWaitCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmployerWaitComment(token,body)) as T
    }

    private suspend fun <T> employerJobFinishReq(
            token: String?,
            body: EmployerJobFinishParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmployerJobFinish(token,body)) as T
    }

    private suspend fun <T> employerOrderDeleteReq(
            token: String?,
            body: EmployerOrderDeleteParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .employerOrderDelete(token,body)) as T
    }

    private suspend fun <T> fetchEmployerFinishUserReq(
            token: String?,
            body: EmployerFinishUserParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
                .fetchEmployerFinishUser(token,body)) as T
    }

    private suspend fun <T> employerWaitCommentUserReq(
        token: String?,
        body: EmployerWaitCommentUserParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .fetchEmployerWaitCommentUser(token,body)) as T
    }

    private suspend fun <T> employerReleasingReq(
        token: String?,
        body: EmployerReleasingParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .fetchEmployerReleasing(token,body)) as T
    }

    private suspend fun <T> unReadStatusReq(
        token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .fetchUnReadStatus(token)) as T
    }

    private suspend fun <T> fetchTaskSettledReq(
        token: String?,
        body: TaskSettledParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .fetchTaskSettlement(token,body)) as T
    }

    private suspend fun <T> checkAutoPrepaidReq(
        token: String?,
        body: CheckAutoPrepaidParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .checkAutoPrepaid(token,body)) as T
    }

    private suspend fun <T> openAutoPrepaidReq(
        token: String?,
        body: OpenAutoPrepaidParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .openAutoPrepaid(token,body)) as T
    }

    private suspend fun <T> closeAutoPrepaidReq(
        token: String?,
        body: CloseAutoPrepaidParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmployerJobApiService::class.java)
            .closeAutoPrepaid(token,body)) as T
    }


}