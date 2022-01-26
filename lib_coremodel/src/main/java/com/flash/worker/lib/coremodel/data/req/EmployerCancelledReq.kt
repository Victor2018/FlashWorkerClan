package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerCancelledInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCancelledReq
 * Author: Victor
 * Date: 2021/2/9 17:28
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCancelledReq: BaseReq() {
    var data: ListData<EmployerCancelledInfo>? = null
}