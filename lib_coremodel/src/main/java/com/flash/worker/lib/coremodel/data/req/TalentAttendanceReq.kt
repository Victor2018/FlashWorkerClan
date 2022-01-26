package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentAttendanceInfo

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentAttendanceReq
 * Author: Victor
 * Date: 2021/4/10 9:52
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentAttendanceReq: BaseReq() {
    var data: ListData<TalentAttendanceInfo>? = null
}