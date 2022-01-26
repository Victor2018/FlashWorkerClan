package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WaitReceiveCountData
 * Author: Victor
 * Date: 2021/6/7 16:27
 * Description: 
 * -----------------------------------------------------------------
 */
class WaitReceiveCountData: Serializable {
    var talentRedPacketCount: Int = 0//人才红包待领取个数
    var employmentRewardCount: Int = 0//雇主奖励待领取个数
}