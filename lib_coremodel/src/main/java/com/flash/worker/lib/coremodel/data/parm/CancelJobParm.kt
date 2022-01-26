package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CancelJobParm
 * Author: Victor
 * Date: 2021/3/30 14:54
 * Description: 
 * -----------------------------------------------------------------
 */
class CancelJobParm: BaseParm() {
    var jobOrderId: String? = null//人才工单ID
    var tradePassword: String? = null//交易密码
}