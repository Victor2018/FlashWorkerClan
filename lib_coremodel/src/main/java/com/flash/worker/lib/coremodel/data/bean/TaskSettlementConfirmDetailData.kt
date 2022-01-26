package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementConfirmDetailData
 * Author: Victor
 * Date: 2021/12/13 14:53
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementConfirmDetailData: Serializable {
    var count: Int = 0//结算人数
    var serviceFeeRate: Double = 0.0//平台服务费比例
    var totalSettledAmount: Double = 0.0//结算总额
    var settlementUsers: List<TaskSettlementConfirmDetailInfo>? = null
}