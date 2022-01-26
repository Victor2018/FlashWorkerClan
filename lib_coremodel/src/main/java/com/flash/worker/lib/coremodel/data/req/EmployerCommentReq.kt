package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerLastCommentInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCommentReq
 * Author: Victor
 * Date: 2021/4/15 17:37
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCommentReq: BaseReq() {
    var data: ListData<EmployerLastCommentInfo>? = null
}