package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementConfirmDetailInfo
 * Author: Victor
 * Date: 2021/12/13 14:55
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementConfirmDetailInfo: Serializable {
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var settledAmount: Double = 0.0//结算金额
    var serviceFeeAmount: Double = 0.0//平台服务费金额
    var totalAmount: Double = 0.0//合计金额
}