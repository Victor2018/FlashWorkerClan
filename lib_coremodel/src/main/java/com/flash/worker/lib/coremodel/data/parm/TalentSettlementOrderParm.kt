package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementOrderParm
 * Author: Victor
 * Date: 2021/3/22 10:58
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentSettlementOrderParm: BaseParm() {
    var settlementStartTime:String? = null //结算开始时间 示例格式:2021.01.18
    var jobOrderId: String? = null //人才工单ID
    var type: Int = 0 //务分类：10-待预付；20-待结算；30-已结算
}