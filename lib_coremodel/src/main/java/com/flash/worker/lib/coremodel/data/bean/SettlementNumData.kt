package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementNumData
 * Author: Victor
 * Date: 2021/3/23 10:41
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementNumData {
    var employerReleaseId: String? = null//雇主发布ID
    var realEmploymentNum: Int = 0//已雇用人数
    var arrivedNum: Int = 0//到岗人数
    var prepaidNum: Int = 0//预付人数
    var finishNum: Int = 0//完工人数
    var settledNum: Int = 0//结算人数
}