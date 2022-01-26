package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettlementOrderReq
 * Author: Victor
 * Date: 2021/3/22 18:06
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettlementOrderReq: BaseReq() {
    var data: ListData<EmployerSettlementOrderInfo>? = null
}