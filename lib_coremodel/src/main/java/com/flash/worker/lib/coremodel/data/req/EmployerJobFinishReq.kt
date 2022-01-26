package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerJobFinishInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobFinishReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobFinishReq: BaseReq() {
    var data: ListData<EmployerJobFinishInfo>? = null
}