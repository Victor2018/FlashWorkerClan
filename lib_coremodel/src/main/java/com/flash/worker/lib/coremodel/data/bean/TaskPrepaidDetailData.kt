package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskPrepaidDetailData
 * Author: Victor
 * Date: 2021/12/6 15:21
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskPrepaidDetailData: Serializable {
    var title: String? = null//任务名称
    var price: Double = 0.0//报酬单价
    var taskQty: Int = 0//任务数量
    var settlementAmount: Double = 0.0//结算金额
    var serviceFeeRate: Double = 0.0//平台服务费比例
    var serviceFeeAmount: Double = 0.0//平台服务费
    var totalPrepaidAmount: Double = 0.0//预付总金额
    var availableBalance: Double = 0.0//可用余额
}