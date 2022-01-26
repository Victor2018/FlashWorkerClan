package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PrepaidConfirmDetailData
 * Author: Victor
 * Date: 2021/3/24 9:44
 * Description: 
 * -----------------------------------------------------------------
 */
class PrepaidConfirmDetailData: Serializable {
    var serviceFeeRate: Double = 0.0//平台服务费比例
    var count: Int = 0//预付人数
    var totalPrepaidAmount: Double = 0.0//预付总额
    var availableBalance: Double = 0.0//可用余额
    var prepaidUsers: List<SalaryTalentInfo>? = null
}