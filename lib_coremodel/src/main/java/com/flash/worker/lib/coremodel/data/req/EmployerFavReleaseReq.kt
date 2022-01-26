package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerFavReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.ListData

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFavReleaseReq
 * Author: Victor
 * Date: 2021/4/23 17:02
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFavReleaseReq: BaseReq() {
    var data: ListData<EmployerFavReleaseInfo>? = null
}