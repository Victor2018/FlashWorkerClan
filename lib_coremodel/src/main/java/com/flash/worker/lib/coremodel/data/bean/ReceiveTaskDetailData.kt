package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReceiveTaskDetailData
 * Author: Victor
 * Date: 2021/12/8 15:39
 * Description: 
 * -----------------------------------------------------------------
 */
class ReceiveTaskDetailData {
    var title: String? = null//任务名称
    var price: Double = 0.0//报酬单价
    var taskReceiveQty: Int = 0//领取数量
    var taskRemainQty: Int = 0//剩余数量
    var settlementAmount: Double = 0.0//结算金额
    var creditFrozenAmount: Double = 0.0//信用冻结金额
    var creditFrozenRate: Int = 0//信用冻结比例
    var availableBalance: Double = 0.0//可用余额
}