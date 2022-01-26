package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentUserInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentUserReq
 * Author: Victor
 * Date: 2021/4/8 14:36
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentUserReq: BaseReq() {
    var data: ListData<EmployerWaitCommentUserInfo>? = null
}