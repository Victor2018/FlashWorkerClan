package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.ITalentJobDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobVM(private val dataSource: ITalentJobDS): ViewModel() {

    val talentWaitEmployData = dataSource.talentWaitEmployData
    fun fetchTalentWaitEmploy(token: String?,body: TalentWaitEmployParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentWaitEmploy(token,body)
        }
    }

    val talentEmployingData = dataSource.talentEmployingData
    fun fetchTalentEmploying(token: String?,body: TalentEmployingParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentEmploying(token,body)
        }
    }

    val talentWaitCommentData = dataSource.talentWaitCommentData
    fun fetchTalentWaitComment(token: String?,body: TalentWaitCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentWaitComment(token,body)
        }
    }

    val talentJobFinishData = dataSource.talentJobFinishData
    fun fetchTalentJobFinish(token: String?,body: TalentJobFinishParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentJobFinish(token,body)
        }
    }

    val talentJobDeleteData = dataSource.talentJobDeleteData
    fun talentJobDelete(token: String?,body: TalentJobDeleteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentJobDelete(token,body)
        }
    }

    val talentOrderDetailData = dataSource.talentOrderDetailData
    fun fetchTalentOrderDetail(token: String?,id: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentOrderDetail(token,id)
        }
    }

    val settlementDateData = dataSource.settlementDateData
    fun fetchSettlementDate(token: String?,employerReleaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSettlementDate(token,employerReleaseId)
        }
    }

    val talentSettlementOrderData = dataSource.talentSettlementOrderData
    fun fetchTalentSettlementOrder(token: String?,body: TalentSettlementOrderParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentSettlementOrder(token,body)
        }
    }

    val cancelJobConfirmDetailData = dataSource.cancelJobConfirmDetailData
    fun fetchCancelJobConfirmDetail(token: String?,body: CancelJobConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchCancelJobConfirmDetail(token,body)
        }
    }

    val cancelJobData = dataSource.cancelJobData
    fun cancelJob(token: String?,body: CancelJobParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelJob(token,body)
        }
    }

    val arrivePostData = dataSource.arrivePostData
    fun arrivePost(token: String?,body: ArrivePostParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.arrivePost(token,body)
        }
    }

    val finishJobData = dataSource.finishJobData
    fun finishJob(token: String?,body: FinishJobParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.finishJob(token,body)
        }
    }

    val remindPrepaidData = dataSource.remindPrepaidData
    fun remindPrepaid(token: String?,body: RemindPrepaidParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.remindPrepaid(token,body)
        }
    }

    val remindSettlementData = dataSource.remindSettlementData
    fun remindSettlement(token: String?,body: RemindSettlementParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.remindSettlement(token,body)
        }
    }

    val unReadStatusData = dataSource.unReadStatusData
    fun fetchUnReadStatus(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUnReadStatus(token)
        }
    }

    val taskSettlementDetailData = dataSource.taskSettlementDetailData
    fun fetchTaskSettlementDetail(token: String?,body: TaskSettlementDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskSettlementDetail(token,body)
        }
    }

    val taskSubmitData = dataSource.taskSubmitData
    fun taskSubmit(token: String?,body: TaskSubmitParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.taskSubmit(token,body)
        }
    }

    val taskSubmitDetailData = dataSource.taskSubmitDetailData
    fun fetchTaskSubmitDetail(token: String?,body: TaskSubmitDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskSubmitDetail(token,body)
        }
    }

}