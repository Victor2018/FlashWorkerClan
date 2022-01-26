package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FrozenFlowInfo
 * Author: Victor
 * Date: 2021/1/29 16:34
 * Description: 
 * -----------------------------------------------------------------
 */
class FrozenFlowInfo {
    var outTradeNo: String? = null//交易流水号
    var tradeTime: String? = null//交易时间
    var tradeAmount: Double = 0.0//交易金额
    var totalBalance: Double = 0.0//当期账户余额
    var tradeType: Int = 0//交易类型：1-收入；2-支出；3-充值；4-提现；5-其他；6-冻结；7-解冻
    var bizName: String? = null//业务名称
    var bizDesc: String? = null//业务描述
}