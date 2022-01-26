package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerEmployingInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEmployingReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEmployingReq: BaseReq() {
    var data: ListData<EmployerEmployingInfo>? = null
}