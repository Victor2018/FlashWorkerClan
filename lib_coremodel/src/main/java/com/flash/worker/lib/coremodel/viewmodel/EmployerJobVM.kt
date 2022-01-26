package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerJobDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobViewModel
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobVM(private val dataSource: IEmployerJobDS): ViewModel() {

    val employerWaitEmployData = dataSource.employerWaitEmployData
    fun fetchWaitEmploy(token: String?,body: EmployerWaitEmployParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchWaitEmploy(token,body)
        }
    }

    val employerSignUpUserData = dataSource.employerSignUpUserData
    fun fetchEmployerSignUpUser(token: String?,body: EmployerSignUpUserParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerSignUpUser(token,body)
        }
    }

    val employerCancelledData = dataSource.employerCancelledData
    fun fetchEmployerCancelled(token: String?,body: EmployerCancelledParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerCancelled(token,body)
        }
    }

    val employerJobDeleteData = dataSource.employerJobDeleteData
    fun employerJobDelete(token: String?,body: EmployerJobDeleteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerJobDelete(token,body)
        }
    }

    val talentUserData = dataSource.talentUserData
    fun fetchTalentUser(token: String?,body: TalentUserParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentUser(token,body)
        }
    }

    val employmentNumData = dataSource.employmentNumData
    fun fetchEmploymentNum(token: String?,employerReleaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmploymentNum(token,employerReleaseId)
        }
    }

    val employerEmployingData = dataSource.employerEmployingData
    fun fetchEmployerEmploying(token: String?,body: EmployerEmployingParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerEmploying(token,body)
        }
    }

    val employerSettlementOrderData = dataSource.employerSettlementOrderData
    fun fetchEmployerSettlementOrder(token: String?,body: EmployerSettlementOrderParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerSettlementOrder(token,body)
        }
    }

    val settlementNumData = dataSource.settlementNumData
    fun fetchSettlementNum(token: String?,body: SettlementNumParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSettlementNum(token,body)
        }
    }

    val fireTalentConfirmDetailData = dataSource.fireTalentConfirmDetailData
    fun fetchFireTalentConfirmDetail(token: String?,body: FireTalentConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchFireTalentConfirmDetail(token,body)
        }
    }

    val fireTalentData = dataSource.fireTalentData
    fun fireTalent(token: String?,body: FireTalentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fireTalent(token,body)
        }
    }

    val employerWaitCommentData = dataSource.employerWaitCommentData
    fun fetchEmployerWaitComment(token: String?,body: EmployerWaitCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerWaitComment(token,body)
        }
    }

    val employerJobFinishData = dataSource.employerJobFinishData
    fun fetchEmployerJobFinish(token: String?,body: EmployerJobFinishParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerJobFinish(token,body)
        }
    }

    val employerOrderDeleteData = dataSource.employerOrderDeleteData
    fun employerOrderDelete(token: String?,body: EmployerOrderDeleteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerOrderDelete(token,body)
        }
    }

    val employerFinishUserData = dataSource.employerFinishUserData
    fun fetchEmployerFinishUser(token: String?,body: EmployerFinishUserParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerFinishUser(token,body)
        }
    }

    val employerWaitCommentUserData = dataSource.employerWaitCommentUserData
    fun fetchEmployerWaitCommentUser(token: String?,body: EmployerWaitCommentUserParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerWaitCommentUser(token,body)
        }
    }

    val employerReleasingData = dataSource.employerReleasingData
    fun fetchEmployerReleasing(token: String?,body: EmployerReleasingParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerReleasing(token,body)
        }
    }

    val unReadStatusData = dataSource.unReadStatusData
    fun fetchUnReadStatus(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUnReadStatus(token)
        }
    }

    val taskSettlementData = dataSource.taskSettledData
    fun fetchTaskSettlement(token: String?,body: TaskSettledParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskSettled(token,body)
        }
    }


}