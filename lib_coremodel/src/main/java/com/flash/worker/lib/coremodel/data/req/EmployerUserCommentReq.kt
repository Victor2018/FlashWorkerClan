package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerUserCommentInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerUserCommentReq
 * Author: Victor
 * Date: 2021/4/19 15:29
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerUserCommentReq: BaseReq() {
    var data: ListData<EmployerUserCommentInfo>? = null
}