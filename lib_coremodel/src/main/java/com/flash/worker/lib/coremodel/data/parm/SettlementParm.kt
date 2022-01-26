package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementParm
 * Author: Victor
 * Date: 2021/3/26 10:16
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementParm: BaseParm() {
    var settlementOrderIds: List<String>? = null//结算工单ID列表
    var tradePassword: String? = null//交易密码
    var employerReleaseId: String? = null//雇主发布ID
}