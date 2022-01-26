package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementConfirmDetailData
 * Author: Victor
 * Date: 2021/3/26 10:13
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementConfirmDetailData: Serializable {
    var count: Int = 0//结算人数
    var totalSettledAmount: Double = 0.0//结算总额
    var serviceFeeRate: Double = 0.0//平台服务费比例
    var settlementUsers: List<SalaryTalentInfo>? = null
    var employerReleaseId: String? = null//雇主发布ID
}