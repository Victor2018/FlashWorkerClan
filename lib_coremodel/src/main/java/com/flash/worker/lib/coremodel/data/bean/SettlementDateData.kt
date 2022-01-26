package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementDateData
 * Author: Victor
 * Date: 2021/3/20 10:35
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementDateData: Serializable {
    var settlementDates: List<SettlementDateInfo>? = null
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var employerReleaseId: String? = null//雇主发布ID
}