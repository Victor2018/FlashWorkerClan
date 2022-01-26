package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementDateInfo
 * Author: Victor
 * Date: 2021/3/20 10:37
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementDateInfo: Serializable {
    var settlementStartTime: String? = null//结算开始时间 示例格式:2021.01.18
    var settlementEndTime: String? = null//结算结束时间 示例格式:2021.01.18
    var isDefualt: Boolean = false//是否是默认时间
}