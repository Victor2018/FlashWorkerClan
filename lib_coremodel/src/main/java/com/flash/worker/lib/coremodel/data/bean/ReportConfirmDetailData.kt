package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportConfirmDetailData
 * Author: Victor
 * Date: 2021/4/27 19:32
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportConfirmDetailData: Serializable {
    var employerUsername: String? = null//雇主闪工名
    var employerName: String? = null//雇主名称
    var compensationCreditAmount: Double = 0.0//保证金赔付金额
    var compensationPrepaidAmount: Double = 0.0//已预付赔付金额

    var taskType: Int = 0//1,工单；2，任务
}