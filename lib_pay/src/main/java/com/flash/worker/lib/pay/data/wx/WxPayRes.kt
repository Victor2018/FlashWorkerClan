package com.flash.worker.lib.pay.data.wx


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WxPayRes
 * Author: Victor
 * Date: 2020/11/30 18:12
 * Description: 
 * -----------------------------------------------------------------
 */
class WxPayRes {
    var appid: String? = null// APPID
    var partnerId: String? = null//商户ID
    var prepayId: String? = null//预支付订单ID
    var nonceStr: String? = null//微信支付特有接口
    var timestamp: String? = null//时间戳
    var sign: String? = null//签名
    var packageValue: String? = null
    var recordNo: String? = null //内部的订单id
}