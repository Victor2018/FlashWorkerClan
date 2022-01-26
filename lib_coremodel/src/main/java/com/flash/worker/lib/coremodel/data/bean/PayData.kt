package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayData
 * Author: Victor
 * Date: 2021/1/14 14:14
 * Description: 
 * -----------------------------------------------------------------
 */
class PayData {
    var channel: String? = null
    var outTradeNo: String? = null
    var alipayResult: AliPayInfo? = null
    var weixinResult: WxPayInfo? = null
}