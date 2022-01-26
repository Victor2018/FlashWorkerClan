package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FinishJobParm
 * Author: Victor
 * Date: 2021/3/30 15:47
 * Description: 
 * -----------------------------------------------------------------
 */
class FinishJobParm: BaseParm() {
    var settlementOrderId: String? = null//结算工单ID
}