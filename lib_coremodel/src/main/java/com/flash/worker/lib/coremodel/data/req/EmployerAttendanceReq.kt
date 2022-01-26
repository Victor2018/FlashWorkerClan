package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerAttendanceInfo
import com.flash.worker.lib.coremodel.data.bean.ListData

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerAttendanceReq
 * Author: Victor
 * Date: 2021/4/10 9:52
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerAttendanceReq: BaseReq() {
    var data: ListData<EmployerAttendanceInfo>? = null
}