package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CompensationNumData
 * Author: Victor
 * Date: 2021/3/30 17:18
 * Description: 
 * -----------------------------------------------------------------
 */
class CompensationNumData {
    var employerReleaseId: String? = null//雇主发布ID
    var receivedCompensationNum:Int = 0//获赔人数
    var compensationNum:Int = 0//赔付人数
    var receivedCompensationAmount: Double = 0.0//获赔金额
    var compensationAmount: Double = 0.0//赔付金额
}