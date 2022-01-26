package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceApi
 * Author: Victor
 * Date: 2021/4/12 20:14
 * Description: 
 * -----------------------------------------------------------------
 */
object AttendanceApi {
    const val TALENT_ATTENDANCE = "jobOrderAttendance/talentGetAttendanceList"
    const val TALENT_ONDUTY = "jobOrderAttendance/onDuty"
    const val TALENT_OFFDUTY = "jobOrderAttendance/offDuty"
    const val EMPLOYER_ATTENDANCE = "jobOrderAttendance/employerGetAttendanceList"
    const val ATTENDANCE_DATE = "jobOrderAttendance/getAttendanceDateList"
    const val EMPLOYER_CONFIRM_ATTENDANCE = "jobOrderAttendance/employerConfirmAttendance"
}