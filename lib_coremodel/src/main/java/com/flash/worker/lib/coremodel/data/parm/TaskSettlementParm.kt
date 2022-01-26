package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementParm
 * Author: Victor
 * Date: 2021/12/14 10:54
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementParm: BaseParm() {
    var employerReleaseId: String? = null//雇主发布ID
    var tradePassword: String? = null//交易密码
    var settlementOrderIds: List<String>? = null//结算工单ID列表
}