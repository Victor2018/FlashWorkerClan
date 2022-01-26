package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IDisputeDS
import com.flash.worker.lib.coremodel.http.service.DisputeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class DisputeDS: AbsDS(),IDisputeDS {

    override val reportConfirmDetailData = MutableLiveData<HttpResult<ReportConfirmDetailReq>>()
    override suspend fun fetchReportConfirmDetail(token: String?,jobOrderId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            reportConfirmDetailData.value = reportConfirmDetailReq(token,jobOrderId)
        }
    }

    override val complaintConfirmDetailData = MutableLiveData<HttpResult<ComplaintConfirmDetailReq>>()
    override suspend fun fetchComplaintConfirmDetail(token: String?,jobOrderId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            complaintConfirmDetailData.value = complaintConfirmDetailReq(token,jobOrderId)
        }
    }

    override val reportData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun report(token: String?,body: ReportParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            reportData.value = reportReq(token,body)
        }
    }

    override val updateReportData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateReport(token: String?,body: ReportParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateReportData.value = updateReportReq(token,body)
        }
    }

    override val complaintData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun complaint(token: String?,body: ComplaintParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            complaintData.value = complaintReq(token,body)
        }
    }

    override val talentDisputeData = MutableLiveData<HttpResult<TalentDisputeReq>>()
    override suspend fun fetchTalentDispute(token: String?,body: TalentDisputeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentDisputeData.value = talentDisputeReq(token,body)
        }
    }

    override val employerDisputeData = MutableLiveData<HttpResult<EmployerDisputeReq>>()
    override suspend fun fetchEmployerDispute(token: String?,body: EmployerDisputeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerDisputeData.value = employerDisputeReq(token,body)
        }
    }

    override val disputeDetailData = MutableLiveData<HttpResult<DisputeDetailReq>>()
    override suspend fun fetchDisputeDetail(token: String?,complaintNo: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            disputeDetailData.value = disputeDetailReq(token,complaintNo)
        }
    }

    override val applyPlatformAccessData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun applyPlatformAccess(token: String?,body: ApplyPlatformAccessParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            applyPlatformAccessData.value = applyPlatformAccessReq(token,body)
        }
    }

    override val cancelComplaintData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelComplaint(token: String?,body: CancelComplaintParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelComplaintData.value = cancelComplaintReq(token,body)
        }
    }

    override val talentDeleteDisputeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun talentDeleteDispute(token: String?,body: TalentDeleteDisputeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentDeleteDisputeData.value = talentDeleteDisputeReq(token,body)
        }
    }

    override val employerDeleteDisputeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerDeleteDispute(token: String?,body: EmployerDeleteDisputeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerDeleteDisputeData.value = employerDeleteDisputeReq(token,body)
        }
    }

    override val agreeComplaintData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun agreeComplaint(token: String?,body: AgreeComplaintParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            agreeComplaintData.value = agreeComplaintReq(token,body)
        }
    }

    override val disputeHistoryData = MutableLiveData<HttpResult<DisputeHistoryReq>>()
    override suspend fun fetchDisputeHistory(token: String?,complaintNo: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            disputeHistoryData.value = disputeHistoryReq(token,complaintNo)
        }
    }

    override val updateComplaintData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateComplaint(token: String?,body: ComplaintParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateComplaintData.value = updateComplaintReq(token,body)
        }
    }

    private suspend fun <T> reportConfirmDetailReq(token: String?,jobOrderId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .fetchReportConfirmDetail(token,jobOrderId)) as T
    }

    private suspend fun <T> complaintConfirmDetailReq(token: String?,jobOrderId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .fetchComplaintConfirmDetail(token,jobOrderId)) as T
    }

    private suspend fun <T> reportReq(token: String?,body: ReportParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .report(token,body)) as T
    }

    private suspend fun <T> updateReportReq(token: String?,body: ReportParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .updateReport(token,body)) as T
    }

    private suspend fun <T> complaintReq(token: String?,body: ComplaintParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .complaint(token,body)) as T
    }

    private suspend fun <T> talentDisputeReq(token: String?,body: TalentDisputeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .fetchTalentDispute(token,body)) as T
    }

    private suspend fun <T> employerDisputeReq(token: String?,body: EmployerDisputeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .fetchEmployerDispute(token,body)) as T
    }

    private suspend fun <T> disputeDetailReq(token: String?,complaintNo: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .fetchDisputeDetail(token,complaintNo)) as T
    }

    private suspend fun <T> applyPlatformAccessReq(token: String?,body: ApplyPlatformAccessParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .applyPlatformAccess(token,body)) as T
    }

    private suspend fun <T> cancelComplaintReq(token: String?,body: CancelComplaintParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .cancelComplaint(token,body)) as T
    }

    private suspend fun <T> talentDeleteDisputeReq(token: String?,body: TalentDeleteDisputeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .talentDeleteDispute(token,body)) as T
    }

    private suspend fun <T> employerDeleteDisputeReq(token: String?,body: EmployerDeleteDisputeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .employerDeleteDispute(token,body)) as T
    }

    private suspend fun <T> agreeComplaintReq(token: String?,body: AgreeComplaintParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
                        .agreeComplaint(token,body)) as T
    }

    private suspend fun <T> disputeHistoryReq(token: String?,complaintNo: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
            .fetchDisputeHistory(token,complaintNo)) as T
    }

    private suspend fun <T> updateComplaintReq(token: String?,body: ComplaintParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(DisputeApiService::class.java)
            .updateComplaint(token,body)) as T
    }


}