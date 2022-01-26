package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayParm
 * Author: Victor
 * Date: 2021/1/14 12:14
 * Description: 
 * -----------------------------------------------------------------
 */
class PayParm: BaseParm() {
    var channel: String? = null//支付渠道：WINXIN,ZFB
    var amount: Double = 0.0//支付金额
}