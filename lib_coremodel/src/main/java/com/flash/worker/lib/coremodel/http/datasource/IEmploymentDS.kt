package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmploymentDS
 * Author: Victor
 * Date: 2021/4/13 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmploymentDS {
    val checkSignUpData: LiveData<HttpResult<CheckSignUpReq>>
    suspend fun checkSignUp(token: String?,body: CheckSignUpParm?)

    val signUpData: LiveData<HttpResult<BaseReq>>
    suspend fun signUp(token: String?,body: SignUpParm?)

    val cancelSignUpData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelSignUp(token: String?,body: TalentCancelSignUpParm?)

    val signUpConfirmDetailData: LiveData<HttpResult<SignUpConfirmDetailReq>>
    suspend fun fetchSignUpConfirmDetail (token: String?,releaseId: String?)

    val employerDetailData: LiveData<HttpResult<EmployerDetailReq>>
    suspend fun fetchEmployerDetail(token: String?,employerId: String?)

    val talentResumeDetailData: LiveData<HttpResult<TalentResumeDetialReq>>
    suspend fun fetchTalentResumeDetail(token: String?,resumeId: String?)

    val employerRefuseData: LiveData<HttpResult<BaseReq>>
    suspend fun employerRefuse(token: String?,body: EmployerRefuseParm?)

    val employConfirmDetailData: LiveData<HttpResult<EmployConfirmDetailReq>>
    suspend fun fetchEmployConfirmDetail(token: String?,body: EmployConfirmDetailParm?)

    val employerEmploymentData: LiveData<HttpResult<BaseReq>>
    suspend fun employerEmployment(token: String?,body: EmployerEmploymentParm?)

    val prepaidConfirmDetailData: LiveData<HttpResult<PrepaidConfirmDetailReq>>
    suspend fun fetchPrepaidConfirmDetail(token: String?,body: PrepaidConfirmDetailParm?)

    val prepaidData: LiveData<HttpResult<PrepaidConfirmDetailReq>>
    suspend fun prepaid(token: String?,body: PrepaidParm?)

    val rewardConfirmDetailData: LiveData<HttpResult<RewardConfirmDetailReq>>
    suspend fun fetchRewardConfirmDetail(token: String?,body: RewardConfirmDetailParm?)

    val rewardData: LiveData<HttpResult<BaseReq>>
    suspend fun reward(token: String?,body: RewardParm?)

    val settlementConfirmDetailData: LiveData<HttpResult<SettlementConfirmDetailReq>>
    suspend fun fetchSettlementConfirmDetail(token: String?,body: SettlementConfirmDetailParm?)

    val settlementData: LiveData<HttpResult<BaseReq>>
    suspend fun settlement(token: String?,body: SettlementParm?)

    val checkTerminationEmploymentData: LiveData<HttpResult<CheckTerminationEmploymentReq>>
    suspend fun checkTerminationEmployment(token: String?,body: CheckTerminationEmploymentParm?)

    val terminationEmploymentData: LiveData<HttpResult<BaseReq>>
    suspend fun terminationEmployment(token: String?,body: TerminationEmploymentParm?)

    val receiveTaskDetailData: LiveData<HttpResult<ReceiveTaskDetailReq>>
    suspend fun fetchReceiveTaskDetail(token: String?,body: ReceiveTaskDetailParm?)

    val receiveTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun receiveTask(token: String?,body: ReceiveTaskParm?)

    val taskSettlementConfirmDetailData: LiveData<HttpResult<TaskSettlementConfirmDetailReq>>
    suspend fun fetchTaskSettlementConfirmDetail(token: String?,body: TaskSettlementConfirmDetailParm?)

    val taskSettlementData: LiveData<HttpResult<BaseReq>>
    suspend fun taskSettlement(token: String?,body: TaskSettlementParm?)

    val checkCloseTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun checkCloseTask(token: String?,body: CheckCloseTaskParm?)

    val closeTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun closeTask(token: String?,body: CloseTaskParm?)

}