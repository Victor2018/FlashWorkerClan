package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponInfo
 * Author: Victor
 * Date: 2021/9/28 21:34
 * Description: 
 * -----------------------------------------------------------------
 */
class UserCouponInfo: Serializable {
    var couponId: String? = null//优惠券ID
    var name: String? = null//优惠券名称
    var amount: Double = 0.0//优惠券金额
    var receiveTime: String? = null//领取时间
    var useTime: String? = null//使用时间
    var expireTime: String? = null//过期时间
    var status: Int = 0//优惠券状态：1-待使用；2-已使用；3-已过期
}