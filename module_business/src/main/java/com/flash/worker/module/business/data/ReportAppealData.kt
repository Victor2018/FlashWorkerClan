package com.flash.worker.module.business.data

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportAppealData
 * Author: Victor
 * Date: 2021/4/26 16:51
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportAppealData: Serializable {
    var id: Int = 0//ID
    var name: String? = null//名称
    var compensationCreditAmount: Double = 0.0
    var compensationPrepaidAmount: Double = 0.0
}