package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementUserInfo
 * Author: Victor
 * Date: 2021/3/26 10:14
 * Description: 
 * -----------------------------------------------------------------
 */
class SalaryTalentInfo: Serializable {
    var username: String? = null//用户名
    var userId: String? = null//用户ID
    var settledAmount: Double = 0.0//结算金额
    var serviceFeeAmount: Double = 0.0//平台服务费金额
    var totalAmount: Double = 0.0//合计总金额
}