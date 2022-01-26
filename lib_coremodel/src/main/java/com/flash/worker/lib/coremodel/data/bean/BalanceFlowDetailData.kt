package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BalanceFlowDetailData
 * Author: Victor
 * Date: 2021/3/31 20:04
 * Description: 
 * -----------------------------------------------------------------
 */
class BalanceFlowDetailData {
    var outTradeNo: String? = null//交易流水号
    var tradeTime: String? = null//交易时间
    var finishTime: String? = null//交易完成时间
    var channel: String? = null//交易渠道
    var bizName: String? = null//业务名称
    var bizDesc: String? = null//业务描述
    var tradeAmount: Double = 0.0//交易金额
    var actualAmount: Double = 0.0//实际到账金额
    var feeAmount: Double = 0.0//第三方支付手续费金额
    var serviceFeeAmount: Double = 0.0//平台服务费金额
    var totalBalance: Double = 0.0//当期账户余额
    var tradeType: Int = 0//交易类型：1-收入；2-支出；3-充值；4-提现；5-其他；6-冻结；7-解冻
    var bizType: Int = 0//业务类型: 10-充值；15-提现；
}