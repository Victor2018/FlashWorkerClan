package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardParm
 * Author: Victor
 * Date: 2021/3/24 19:42
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardParm: BaseParm(), Serializable {
    var settlementOrderId: String? = null//结算工单ID
    var rewardAmount: Int = 0//奖励金额
    var tradePassword: String? = null//交易密码
    var rewardLabel: List<String>? = null
}