package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IDisputeDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DisputeVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class DisputeVM(private val dataSource: IDisputeDS): ViewModel() {

    val reportConfirmDetailData = dataSource.reportConfirmDetailData
    fun fetchReportConfirmDetail(token: String?,jobOrderId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchReportConfirmDetail(token,jobOrderId)
        }
    }

    val complaintConfirmDetailData = dataSource.complaintConfirmDetailData
    fun fetchComplaintConfirmDetail(token: String?,jobOrderId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchComplaintConfirmDetail(token,jobOrderId)
        }
    }

    val reportData = dataSource.reportData
    fun report(token: String?,body: ReportParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.report(token,body)
        }
    }

    val updateReportData = dataSource.updateReportData
    fun updateReport(token: String?,body: ReportParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateReport(token,body)
        }
    }

    val complaintData = dataSource.complaintData
    fun complaint(token: String?,body: ComplaintParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.complaint(token,body)
        }
    }

    val talentDisputeData = dataSource.talentDisputeData
    fun fetchTalentDispute(token: String?,body: TalentDisputeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentDispute(token,body)
        }
    }

    val employerDisputeData = dataSource.employerDisputeData
    fun fetchEmployerDispute(token: String?,body: EmployerDisputeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerDispute(token,body)
        }
    }

    val disputeDetailData = dataSource.disputeDetailData
    fun fetchDisputeDetail(token: String?,complaintNo: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchDisputeDetail(token,complaintNo)
        }
    }

    val applyPlatformAccessData = dataSource.applyPlatformAccessData
    fun applyPlatformAccess(token: String?,body: ApplyPlatformAccessParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.applyPlatformAccess(token,body)
        }
    }

    val cancelComplaintData = dataSource.cancelComplaintData
    fun cancelComplaint(token: String?,body: CancelComplaintParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelComplaint(token,body)
        }
    }

    val talentDeleteDisputeData = dataSource.talentDeleteDisputeData
    fun talentDeleteDispute(token: String?,body: TalentDeleteDisputeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentDeleteDispute(token,body)
        }
    }

    val employerDeleteDisputeData = dataSource.employerDeleteDisputeData
    fun employerDeleteDispute(token: String?,body: EmployerDeleteDisputeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerDeleteDispute(token,body)
        }
    }

    val agreeComplaintData = dataSource.agreeComplaintData
    fun agreeComplaint(token: String?,body: AgreeComplaintParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.agreeComplaint(token,body)
        }
    }

    val disputeHistoryData = dataSource.disputeHistoryData
    fun fetchDisputeHistory(token: String?,complaintNo: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchDisputeHistory(token,complaintNo)
        }
    }

    val updateComplaintData = dataSource.updateComplaintData
    fun updateComplaint(token: String?,body: ComplaintParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateComplaint(token,body)
        }
    }

}