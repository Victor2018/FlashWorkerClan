package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.datasource.IEmploymentDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentVM(private val dataSource: IEmploymentDS): ViewModel() {

    val checkSignUpData = dataSource.checkSignUpData
    fun checkSignUp(token: String?,body: CheckSignUpParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkSignUp(token,body)
        }
    }

    val signUpData = dataSource.signUpData
    fun signUp(token: String?,body: SignUpParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.signUp(token,body)
        }
    }

    val cancelSignUpData = dataSource.cancelSignUpData
    fun cancelSignUp(token: String?,body: TalentCancelSignUpParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelSignUp(token,body)
        }
    }

    val signUpConfirmDetailData = dataSource.signUpConfirmDetailData
    fun fetchSignUpConfirmDetail(token: String?,releaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSignUpConfirmDetail(token,releaseId)
        }
    }

    val employerDetailData = dataSource.employerDetailData
    fun fetchEmployerDetail(token: String?,employerId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerDetail(token,employerId)
        }
    }

    val talentResumeDetailData = dataSource.talentResumeDetailData
    fun fetchTalentResumeDetail(token: String?,resumeId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentResumeDetail(token,resumeId)
        }
    }

    val employerRefuseData = dataSource.employerRefuseData
    fun employerRefuse(token: String?,body: EmployerRefuseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerRefuse(token,body)
        }
    }

    val employConfirmDetailData = dataSource.employConfirmDetailData
    fun fetchEmployConfirmDetail(token: String?,body: EmployConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployConfirmDetail(token,body)
        }
    }

    val employerEmploymentData = dataSource.employerEmploymentData
    fun employerEmployment(token: String?,body: EmployerEmploymentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerEmployment(token,body)
        }
    }

    val prepaidConfirmDetailData = dataSource.prepaidConfirmDetailData
    fun fetchPrepaidConfirmDetail(token: String?,body: PrepaidConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchPrepaidConfirmDetail(token,body)
        }
    }

    val prepaidData = dataSource.prepaidData
    fun prepaid(token: String?,body: PrepaidParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.prepaid(token,body)
        }
    }

    val rewardConfirmDetailData = dataSource.rewardConfirmDetailData
    fun fetchRewardConfirmDetail(token: String?,body: RewardConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchRewardConfirmDetail(token,body)
        }
    }

    val rewardData = dataSource.rewardData
    fun reward(token: String?,body: RewardParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.reward(token,body)
        }
    }

    val settlementConfirmDetailData = dataSource.settlementConfirmDetailData
    fun fetchSettlementConfirmDetail(token: String?,body: SettlementConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSettlementConfirmDetail(token,body)
        }
    }

    val settlementData = dataSource.settlementData
    fun settlement(token: String?,body: SettlementParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.settlement(token,body)
        }
    }

    val checkTerminationEmploymentData = dataSource.checkTerminationEmploymentData
    fun checkTerminationEmployment(token: String?,body: CheckTerminationEmploymentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkTerminationEmployment(token,body)
        }
    }

    val terminationEmploymentData = dataSource.terminationEmploymentData
    fun terminationEmployment(token: String?,body: TerminationEmploymentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.terminationEmployment(token,body)
        }
    }

    val receiveTaskDetailData = dataSource.receiveTaskDetailData
    fun fetchReceiveTaskDetail(token: String?,body: ReceiveTaskDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchReceiveTaskDetail(token,body)
        }
    }

    val receiveTaskData = dataSource.receiveTaskData
    fun receiveTask(token: String?,body: ReceiveTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.receiveTask(token,body)
        }
    }

    val taskSettlementConfirmDetailData = dataSource.taskSettlementConfirmDetailData
    fun fetchTaskSettlementConfirmDetail(token: String?,body: TaskSettlementConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskSettlementConfirmDetail(token,body)
        }
    }

    val taskSettlementData = dataSource.taskSettlementData
    fun taskSettlement(token: String?,body: TaskSettlementParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.taskSettlement(token,body)
        }
    }

    val checkCloseTaskData = dataSource.checkCloseTaskData
    fun checkCloseTask(token: String?,body: CheckCloseTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkCloseTask(token,body)
        }
    }

    val closeTaskData = dataSource.closeTaskData
    fun closeTask(token: String?,body: CloseTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.closeTask(token,body)
        }
    }

}