package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementDetailParm
 * Author: Victor
 * Date: 2021/12/9 11:43
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementDetailParm: BaseParm() {
    var jobOrderId: String? = null//人才工单ID
    var type: Int = 0//业务分类：10-已领取；20-已提交；
}