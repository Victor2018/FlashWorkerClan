package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OpenUserUnBindParm
 * Author: Victor
 * Date: 2021/2/10 17:14
 * Description: 
 * -----------------------------------------------------------------
 */
class OpenUserUnBindParm: BaseParm() {
    var channel: String? = null//渠道名称：weixin-微信；alipay-支付宝
    var tradePassword: String? = null//交易密码
}