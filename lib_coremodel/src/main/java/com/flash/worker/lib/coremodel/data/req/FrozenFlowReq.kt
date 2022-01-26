package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.FrozenFlowInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FrozenFlowReq
 * Author: Victor
 * Date: 2021/1/29 15:55
 * Description: 
 * -----------------------------------------------------------------
 */
class FrozenFlowReq: BaseReq() {
    var data: ListData<FrozenFlowInfo>? = null
}