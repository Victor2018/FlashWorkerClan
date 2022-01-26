package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEmploymentParm
 * Author: Victor
 * Date: 2021/3/19 14:20
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEmploymentParm: BaseParm() {
    var employerReleaseId: String? = null//雇主发布ID
    var jobOrderIds: List<String>? = null//人才工单ID
    var tradePassword: String? = null//交易密码


}