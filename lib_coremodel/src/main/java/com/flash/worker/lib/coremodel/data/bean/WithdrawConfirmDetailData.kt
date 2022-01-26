package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawConfirmDetailData
 * Author: Victor
 * Date: 2021/3/31 17:31
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawConfirmDetailData {
    var withdrawAmount: Double = 0.0//提现金额
    var feeAmount: Double = 0.0//手续费金额
    var actualAmount: Double = 0.0//实际到账金额金额
    var withdrawRate: Double = 0.0//提现手续费费率(百分比)
    var channel: String? = null//渠道名称：weixin-微信；alipay-支付宝


}