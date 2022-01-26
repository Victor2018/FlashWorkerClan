package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerReleasingInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleasingReq
 * Author: Victor
 * Date: 2021/4/19 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleasingReq: BaseReq() {
    var data: ListData<EmployerReleasingInfo>? = null
}