package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseReq
 * Author: Victor
 * Date: 2021/1/20 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleaseReq: BaseReq() {
    var data: ListData<EmployerReleaseInfo>? = null
}