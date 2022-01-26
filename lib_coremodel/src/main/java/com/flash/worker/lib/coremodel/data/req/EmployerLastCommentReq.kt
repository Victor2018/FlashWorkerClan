package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerLastCommentInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerLastCommentReq
 * Author: Victor
 * Date: 2021/4/15 17:30
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerLastCommentReq: BaseReq() {
    var data: List<EmployerLastCommentInfo>? = null
}