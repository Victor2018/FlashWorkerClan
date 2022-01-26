package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerSignUpUserInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSignUpUserReq
 * Author: Victor
 * Date: 2021/2/9 15:02
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSignUpUserReq: BaseReq() {
    var data: ListData<EmployerSignUpUserInfo>? = null
}