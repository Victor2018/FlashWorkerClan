package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ViolationLabelInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerViolationLabelReq
 * Author: Victor
 * Date: 2021/7/15 17:18
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationLabelReq: BaseReq() {
    var data: List<ViolationLabelInfo>? = null
}