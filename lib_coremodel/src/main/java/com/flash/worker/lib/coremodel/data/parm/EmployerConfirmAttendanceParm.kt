package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerConfirmAttendanceParm
 * Author: Victor
 * Date: 2021/4/12 11:23
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerConfirmAttendanceParm: BaseParm() {
    var attendanceId: String? = null//考勤ID
    var attendanceType: Int = 0//考勤类型：1-上班考勤；2-下班考勤
}