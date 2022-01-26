package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerAttendanceInfo
 * Author: Victor
 * Date: 2021/4/10 9:54
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerAttendanceInfo {
    var attendanceId: String? = null//考勤ID
    var jobOrderId: String? = null//人才工单ID
    var talentUserId: String? = null//人才用户ID
    var username: String? = null//用户名
    var attendanceDate: String? = null//考勤基准日期
    var onDutyTime: String? = null//上班时间
    var offDutyTime: String? = null//下班时间
    var onDutyStatus: Int = 0//上班考勤状态：1-待打卡；2-未打卡；3-正常；4-迟到
    var offDutyStatus: Int = 0//下班考勤状态：1-待打卡；2-未打卡；3-正常；4-早退
    var onDutyConfirmStatus: Int = 0//上班考勤确认：1-默认；2-有异议
    var offDutyConfirmStatus: Int = 0//下班考勤确认：1-默认；2-有异议
    var onDutyShift: Int = 0//上班时间班次：1-当天打卡；2-次日打卡
    var offDutyShift: Int = 0//下班时间班次：1-当天打卡；2-次日打卡
}