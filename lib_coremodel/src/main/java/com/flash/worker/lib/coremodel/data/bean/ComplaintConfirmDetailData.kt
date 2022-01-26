package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ComplaintConfirmDetailData
 * Author: Victor
 * Date: 2021/4/27 19:32
 * Description: 
 * -----------------------------------------------------------------
 */
class ComplaintConfirmDetailData: Serializable {
    var talentUsername: String? = null//人才闪工名
    var compensationCreditAmount: Double = 0.0//保证金赔付金额
    var compensationPrepaidAmount: Double = 0.0//已预付赔付金额
}