package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.service.EmploymentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentDS
 * Author: Victor
 * Date: 2021/4/13 11:37
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentDS: AbsDS(),IEmploymentDS {

    override val checkSignUpData = MutableLiveData<HttpResult<CheckSignUpReq>>()
    override suspend fun checkSignUp(token: String?,body: CheckSignUpParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkSignUpData.value = checkSignUpReq(token,body)
        }
    }

    override val signUpData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun signUp(token: String?,body: SignUpParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            signUpData.value = signUpReq(token,body)
        }
    }

    override val cancelSignUpData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelSignUp(token: String?,body: TalentCancelSignUpParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelSignUpData.value = talentCancelSignUpReq(token,body)
        }
    }

    override val signUpConfirmDetailData = MutableLiveData<HttpResult<SignUpConfirmDetailReq>>()
    override suspend fun fetchSignUpConfirmDetail(token: String?,releaseId : String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            signUpConfirmDetailData.value = fetchSignUpConfrimDetailReq(token,releaseId)
        }
    }

    override val employerDetailData = MutableLiveData<HttpResult<EmployerDetailReq>>()
    override suspend fun fetchEmployerDetail(token: String?,employerId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerDetailData.value = fetchEmployerDetailReq(token,employerId)
        }
    }

    override val talentResumeDetailData = MutableLiveData<HttpResult<TalentResumeDetialReq>>()
    override suspend fun fetchTalentResumeDetail(token: String?,resumeId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentResumeDetailData.value = talentResumeDetailReq(token,resumeId)
        }
    }

    override val employerRefuseData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerRefuse(token: String?,body: EmployerRefuseParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerRefuseData.value = employerRefuseReq(token,body)
        }
    }

    override val employConfirmDetailData = MutableLiveData<HttpResult<EmployConfirmDetailReq>>()
    override suspend fun fetchEmployConfirmDetail(token: String?,body: EmployConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employConfirmDetailData.value = fetchEmployConfrimDetailReq(token,body)
        }
    }

    override val employerEmploymentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerEmployment(token: String?,body: EmployerEmploymentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerEmploymentData.value = employerEmploymentReq(token,body)
        }
    }

    override val prepaidConfirmDetailData = MutableLiveData<HttpResult<PrepaidConfirmDetailReq>>()
    override suspend fun fetchPrepaidConfirmDetail(token: String?,body: PrepaidConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            prepaidConfirmDetailData.value = prepaidConfirmDetailReq(token,body)
        }
    }

    override val prepaidData = MutableLiveData<HttpResult<PrepaidConfirmDetailReq>>()
    override suspend fun prepaid(token: String?,body: PrepaidParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            prepaidData.value = prepaidReq(token,body)
        }
    }

    override val rewardConfirmDetailData = MutableLiveData<HttpResult<RewardConfirmDetailReq>>()
    override suspend fun fetchRewardConfirmDetail(token: String?,body: RewardConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            rewardConfirmDetailData.value = rewardConfirmDetailReq(token,body)
        }
    }

    override val rewardData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun reward(token: String?,body: RewardParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            rewardData.value = rewardReq(token,body)
        }
    }

    override val settlementConfirmDetailData = MutableLiveData<HttpResult<SettlementConfirmDetailReq>>()
    override suspend fun fetchSettlementConfirmDetail(token: String?,body: SettlementConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            settlementConfirmDetailData.value = settlementConfirmDetailReq(token,body)
        }
    }

    override val settlementData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun settlement(token: String?,body: SettlementParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            settlementData.value = settlementReq(token,body)
        }
    }

    override val checkTerminationEmploymentData = MutableLiveData<HttpResult<CheckTerminationEmploymentReq>>()
    override suspend fun checkTerminationEmployment(token: String?,body: CheckTerminationEmploymentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkTerminationEmploymentData.value = checkTerminationEmploymentReq(token,body)
        }
    }

    override val terminationEmploymentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun terminationEmployment(token: String?,body: TerminationEmploymentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            terminationEmploymentData.value = terminationEmploymentReq(token,body)
        }
    }

    override val receiveTaskDetailData = MutableLiveData<HttpResult<ReceiveTaskDetailReq>>()
    override suspend fun fetchReceiveTaskDetail(token: String?,body: ReceiveTaskDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveTaskDetailData.value = fetchReceiveTaskDetailReq(token,body)
        }
    }

    override val receiveTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun receiveTask(token: String?,body: ReceiveTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveTaskData.value = receiveTaskReq(token,body)
        }
    }

    override val taskSettlementConfirmDetailData = MutableLiveData<HttpResult<TaskSettlementConfirmDetailReq>>()
    override suspend fun fetchTaskSettlementConfirmDetail(token: String?,body: TaskSettlementConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSettlementConfirmDetailData.value = fetchTaskSettlementConfirmDetailReq(token,body)
        }
    }

    override val taskSettlementData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun taskSettlement(token: String?,body: TaskSettlementParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskSettlementData.value = taskSettlementReq(token,body)
        }
    }

    override val checkCloseTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun checkCloseTask(token: String?,body: CheckCloseTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkCloseTaskData.value = checkCloseTaskReq(token,body)
        }
    }

    override val closeTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun closeTask(token: String?,body: CloseTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            closeTaskData.value = closeTaskReq(token,body)
        }
    }

    private suspend fun <T> checkSignUpReq(token: String?,body: CheckSignUpParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .checkSingUp(token,body)) as T
    }

    private suspend fun <T> signUpReq(token: String?,body: SignUpParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .singUp(token,body)) as T
    }

    private suspend fun <T> talentCancelSignUpReq(
            token: String?,
            body: TalentCancelSignUpParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .talentCancelSignUp(token,body)) as T
    }

    private suspend fun <T> fetchSignUpConfrimDetailReq(token: String?,releaseId : String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchSignUpConfirmDetail(token,releaseId)) as T
    }

    private suspend fun <T> fetchEmployerDetailReq(
            token: String?,
            employerId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchEmployerDetail(token,employerId)) as T
    }

    private suspend fun <T> talentResumeDetailReq(
            token: String?,
            resumeId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchTalentResumeDetail(token,resumeId)) as T
    }

    private suspend fun <T> employerRefuseReq(
            token: String?,
            body: EmployerRefuseParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .employerRefuse(token,body)) as T
    }

    private suspend fun <T> fetchEmployConfrimDetailReq(token: String?,body : EmployConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchEmployConfirmDetail(token,body)) as T
    }

    private suspend fun <T> employerEmploymentReq(token: String?,body : EmployerEmploymentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .employerEmployment(token,body)) as T
    }

    private suspend fun <T> prepaidConfirmDetailReq(
            token: String?,
            body: PrepaidConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchPrepaidConfirmDetail(token,body)) as T
    }

    private suspend fun <T> prepaidReq(
            token: String?,
            body: PrepaidParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .prepaid(token,body)) as T
    }

    private suspend fun <T> rewardConfirmDetailReq(
            token: String?,
            body: RewardConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchRewardConfirmDetail(token,body)) as T
    }

    private suspend fun <T> rewardReq(
            token: String?,
            body: RewardParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .reward(token,body)) as T
    }

    private suspend fun <T> settlementConfirmDetailReq(
            token: String?,
            body: SettlementConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .settlementConfirmDetail(token,body)) as T
    }

    private suspend fun <T> settlementReq(
            token: String?,
            body: SettlementParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .settlement(token,body)) as T
    }

    private suspend fun <T> checkTerminationEmploymentReq(
            token: String?,
            body: CheckTerminationEmploymentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .checkTerminationEmployment(token,body)) as T
    }

    private suspend fun <T> terminationEmploymentReq(
            token: String?,
            body: TerminationEmploymentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .terminationEmployment(token,body)) as T
    }

    private suspend fun <T> fetchReceiveTaskDetailReq(
            token: String?,
            body: ReceiveTaskDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchReceiveTaskDetail(token,body)) as T
    }

    private suspend fun <T> receiveTaskReq(
            token: String?,
            body: ReceiveTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .receiveTask(token,body)) as T
    }

    private suspend fun <T> fetchTaskSettlementConfirmDetailReq(
            token: String?,
            body: TaskSettlementConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .fetchTaskSettlementConfirmDetail(token,body)) as T
    }

    private suspend fun <T> taskSettlementReq(
            token: String?,
            body: TaskSettlementParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .taskSettlement(token,body)) as T
    }

    private suspend fun <T> checkCloseTaskReq(
            token: String?,
            body: CheckCloseTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .checkCloseTask(token,body)) as T
    }

    private suspend fun <T> closeTaskReq(
            token: String?,
            body: CloseTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(EmploymentApiService::class.java)
                .closeTask(token,body)) as T
    }

}