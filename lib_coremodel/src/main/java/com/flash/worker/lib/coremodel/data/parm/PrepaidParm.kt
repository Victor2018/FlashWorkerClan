package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PrepaidWagesParm
 * Author: Victor
 * Date: 2021/3/24 10:57
 * Description: 
 * -----------------------------------------------------------------
 */
class PrepaidParm: BaseParm() {
    var employerReleaseId: String? = null//雇主发布ID
    var settlementOrderIds: List<String>? = null//结算工单ID列表
    var tradePassword: String? = null//交易密码


}