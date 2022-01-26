package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardConfirmDetailParm
 * Author: Victor
 * Date: 2021/3/24 19:32
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardConfirmDetailParm: BaseParm() {
    var settlementOrderId: String? = null//结算工单ID
    var rewardAmount: Int = 0//奖励金额
    var rewardLable: List<String>? = null

}