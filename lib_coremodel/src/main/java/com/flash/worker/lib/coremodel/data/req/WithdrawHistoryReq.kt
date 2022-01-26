package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.WithdrawHistoryInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawHistoryReq
 * Author: Victor
 * Date: 2021/11/3 15:05
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawHistoryReq: BaseReq() {
    var data: ListData<WithdrawHistoryInfo>? = null
}