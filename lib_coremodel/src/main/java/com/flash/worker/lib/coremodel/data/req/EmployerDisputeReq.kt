package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerDisputeInfo
import com.flash.worker.lib.coremodel.data.bean.ListData

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDisputeReq
 * Author: Victor
 * Date: 2021/4/27 19:38
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerDisputeReq: BaseReq() {
    var data: ListData<EmployerDisputeInfo>? = null
}