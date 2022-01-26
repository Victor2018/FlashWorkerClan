package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BindPayAccountParm
 * Author: Victor
 * Date: 2021/1/29 15:16
 * Description: 
 * -----------------------------------------------------------------
 */
class BindAliPayParm: BaseParm() {
    var code: String? = null//授权码
    var channel: String? = null//渠道名称：weixin-微信；alipay-支付宝
}