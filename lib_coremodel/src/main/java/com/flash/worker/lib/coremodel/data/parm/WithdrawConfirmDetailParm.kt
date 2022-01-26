package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawConfirmDetailParm
 * Author: Victor
 * Date: 2021/3/31 17:30
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawConfirmDetailParm: BaseParm() {
    var withdrawAmount: Double = 0.0//提现金额
    var channel: String? = null//渠道名称：weixin-微信；alipay-支付宝
}