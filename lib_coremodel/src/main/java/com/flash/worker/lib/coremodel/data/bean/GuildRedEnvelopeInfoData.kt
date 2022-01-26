package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeInfoData
 * Author: Victor
 * Date: 2021/5/25 17:32
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildRedEnvelopeInfoData {
    var amount: Double = 0.0//红包金额
    var remainQty: Int =  0//剩余数量
    var status: Int =  0//1-待领取；2-已领取；3-已结束（已领完）
}