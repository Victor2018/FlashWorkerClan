package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportParm
 * Author: Victor
 * Date: 2021/4/27 19:34
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportParm: BaseParm() {
    var jobOrderId: String? = null//人才工单ID
    var complaintItems: List<String>? = null//举报事项
    var complaintPics: List<String>? = null//相关凭证
    var applyCompensationCreditAmount: Double? = null//要求保证金赔付金额
    var applyCompensationPrepaidAmount: Double? = null//要求结算已预付金额
}