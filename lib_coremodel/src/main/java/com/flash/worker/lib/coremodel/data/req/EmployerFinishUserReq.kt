package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerFinishUserInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFinishUserReq
 * Author: Victor
 * Date: 2021/4/7 14:26
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFinishUserReq: BaseReq() {
    var data: ListData<EmployerFinishUserInfo>? = null
}