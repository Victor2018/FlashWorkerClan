package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementNumParm
 * Author: Victor
 * Date: 2021/3/23 10:40
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementNumParm: BaseParm() {
    var settlementStartTime: String? = null//结算开始时间 示例格式:2021.01.18
    var employerReleaseId: String? = null//雇主发布ID
}