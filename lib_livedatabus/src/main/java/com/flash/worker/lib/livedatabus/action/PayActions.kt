package com.flash.worker.lib.livedatabus.action


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayActions
 * Author: Victor
 * Date: 2020/12/11 17:09
 * Description: 
 * -----------------------------------------------------------------
 */
object PayActions {
    /**
     * 微信支付成功回调
     */
    const val EVENT_WXPAY_RESULT = "EVENT_WXPAY_RESULT"

    /**
     * 支付宝支付成功回调
     */
    const val EVENT_ALIPAY_RESULT = "EVENT_ALIPAY_RESULT"
}