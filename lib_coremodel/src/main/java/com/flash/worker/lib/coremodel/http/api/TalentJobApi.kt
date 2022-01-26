package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobApi
 * Author: Victor
 * Date: 2021/4/12 20:12
 * Description: 
 * -----------------------------------------------------------------
 */
object TalentJobApi {
    const val TALENT_WAIT_EMPLOY = "talentJob/waitEmploymentList"
    const val TALENT_EMPLOYING = "talentJob/employmentList"
    const val TALENT_WAIT_COMMENT = "talentJob/waitCommentList"
    const val TALENT_JOB_FINISH = "talentJob/finishList"
    const val TALENT_ORDER_DELETE = "talentJob/delHistoryOrder"
    const val TALENT_ORDER_DETAIL = "talentJob/getJobOrderDetail"
    const val SETTLEMENT_DATE = "talentJob/getSettlementDateList"
    const val TALENT_SETTLEMENT_ORDER = "talentJob/getSettlementOrder"
    const val CANCEL_JOB_CONFIRM_DETAIL = "talentJob/cancelJobConfirmDetail"
    const val CANCEL_JOB = "talentJob/cancelJob"
    const val ARRIVE_POST = "talentJob/arrivePost"
    const val FINISH_JOB = "talentJob/finishJob"
    const val REMIND_PREPAID = "talentJob/remindPrepaid"
    const val REMIND_SETTLEMENT = "talentJob/remindSettlement"
    const val UNREAD_STATUS = "talentJob/getUnreadStatus"
    const val TASK_SETTLEMENT_DETAIL = "talentJob/taskSettlementDetail"
    const val TASK_SUBMIT = "talentJob/taskSubmit"
    const val TASK_SUBMIT_DETAIL = "talentJob/taskSubmitDetail"
}