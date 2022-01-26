package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.BalanceFlowInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BalanceFlowReq
 * Author: Victor
 * Date: 2021/1/29 15:55
 * Description: 
 * -----------------------------------------------------------------
 */
class BalanceFlowReq: BaseReq() {
    var data: ListData<BalanceFlowInfo>? = null
}