package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawParm
 * Author: Victor
 * Date: 2021/2/1 20:18
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawParm: BaseParm() {
    var withdrawAmount: Double = 0.0//提现金额
    var channel: String? = null//渠道名称：weixin-微信；alipay-支付宝
    var tradePassword: String? = null//交易密码

}