package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmploymentRewardInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentRewardReq
 * Author: Victor
 * Date: 2021/6/1 10:10
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentRewardReq: BaseReq() {
    var data: ListData<EmploymentRewardInfo>? = null
}