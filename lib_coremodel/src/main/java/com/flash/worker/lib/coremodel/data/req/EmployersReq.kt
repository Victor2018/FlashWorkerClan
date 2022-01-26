package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerInfo
import com.flash.worker.lib.coremodel.data.req.BaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployersReq
 * Author: Victor
 * Date: 2021/1/18 17:43
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployersReq: BaseReq() {
    var data: List<EmployerInfo>? = null
}