package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerCommentCenterInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCommentCenterReq
 * Author: Victor
 * Date: 2021/4/13 16:43
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCommentCenterReq: BaseReq() {
    var data: ListData<EmployerCommentCenterInfo>? = null
}