package com.flash.worker.lib.common.data


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementInfo
 * Author: Victor
 * Date: 2021/2/3 10:53
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementInfo {
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var settlementName: String? = null//结算方式名称
}