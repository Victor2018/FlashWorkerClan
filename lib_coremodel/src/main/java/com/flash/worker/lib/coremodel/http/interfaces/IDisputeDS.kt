package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IAccountDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IDisputeDS {

    val reportConfirmDetailData: LiveData<HttpResult<ReportConfirmDetailReq>>
    suspend fun fetchReportConfirmDetail(token: String?,jobOrderId: String?)

    val complaintConfirmDetailData: LiveData<HttpResult<ComplaintConfirmDetailReq>>
    suspend fun fetchComplaintConfirmDetail(token: String?,jobOrderId: String?)

    val reportData: LiveData<HttpResult<BaseReq>>
    suspend fun report(token: String?,body: ReportParm?)

    val updateReportData: LiveData<HttpResult<BaseReq>>
    suspend fun updateReport(token: String?,body: ReportParm?)

    val complaintData: LiveData<HttpResult<BaseReq>>
    suspend fun complaint(token: String?,body: ComplaintParm?)

    val talentDisputeData: LiveData<HttpResult<TalentDisputeReq>>
    suspend fun fetchTalentDispute(token: String?,body: TalentDisputeParm?)

    val employerDisputeData: LiveData<HttpResult<EmployerDisputeReq>>
    suspend fun fetchEmployerDispute(token: String?,body: EmployerDisputeParm?)

    val disputeDetailData: LiveData<HttpResult<DisputeDetailReq>>
    suspend fun fetchDisputeDetail(token: String?,complaintNo: String?)

    val applyPlatformAccessData: LiveData<HttpResult<BaseReq>>
    suspend fun applyPlatformAccess(token: String?,body: ApplyPlatformAccessParm?)

    val cancelComplaintData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelComplaint(token: String?,body: CancelComplaintParm?)

    val talentDeleteDisputeData: LiveData<HttpResult<BaseReq>>
    suspend fun talentDeleteDispute(token: String?,body: TalentDeleteDisputeParm?)

    val employerDeleteDisputeData: LiveData<HttpResult<BaseReq>>
    suspend fun employerDeleteDispute(token: String?,body: EmployerDeleteDisputeParm?)

    val agreeComplaintData: LiveData<HttpResult<BaseReq>>
    suspend fun agreeComplaint(token: String?,body: AgreeComplaintParm?)

    val disputeHistoryData: LiveData<HttpResult<DisputeHistoryReq>>
    suspend fun fetchDisputeHistory(token: String?,complaintNo: String?)

    val updateComplaintData: LiveData<HttpResult<BaseReq>>
    suspend fun updateComplaint(token: String?,body: ComplaintParm?)

}