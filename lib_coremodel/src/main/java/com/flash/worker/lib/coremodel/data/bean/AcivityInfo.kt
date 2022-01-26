package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AcivityInfo
 * Author: Victor
 * Date: 2021/5/31 17:18
 * Description: 
 * -----------------------------------------------------------------
 */
class AcivityInfo {
    var configId: String? = null//红包配置ID
    var redPacketAmount: Double = 0.0//红包金额
    var redPacketDesc: String? = null//红包说明
    var redPacketQty: Int = 0//红包数量
    var receiveCount: Int = 0//已领取个数
    var remainCount: Int = 0//当前可领取个数
}