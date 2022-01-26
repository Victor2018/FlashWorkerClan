package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeApi
 * Author: Victor
 * Date: 2021/4/12 20:11
 * Description: 
 * -----------------------------------------------------------------
 */
object DisputeApi {
    const val REPORT_CONFIRM_DETAIL = "jobOrderDispute/talentReportConfirmDetail"
    const val COMPLAINT_CONFIRM_DETAIL = "jobOrderDispute/employerComplaintConfirmDetail"

    const val REPORT = "jobOrderDispute/talentReport"
    const val COMPLAINT = "jobOrderDispute/employerComplaint"

    const val UPDATE_REPORT = "jobOrderDispute/talentUpdateReport"
    const val UPDATE_COMPLAINT = "jobOrderDispute/employerUpdateComplaint"

    const val TALENT_DISPUTE = "jobOrderDispute/talentDisputeList"
    const val EMPLOYER_DISPUTE = "jobOrderDispute/employerDisputeList"

    const val DISPUTE_DETAIL = "jobOrderDispute/getDisputeDetail"
    const val APPLY_PLATFORM_ACCESS = "jobOrderDispute/applyPlatformArbitration"
    const val CANCEL_COMPLAINT = "jobOrderDispute/cancelComplaint"

    const val TALENT_DELETE_DISPUTE = "jobOrderDispute/talentDeleteOrder"
    const val EMPLOYER_DELETE_DISPUTE = "jobOrderDispute/employerDeleteOrder"
    const val AGREE_COMPLAINT = "jobOrderDispute/agreeComplaint"
    const val DISPUTE_HISTORY = "jobOrderDispute/getDisputeHistoryList"
}